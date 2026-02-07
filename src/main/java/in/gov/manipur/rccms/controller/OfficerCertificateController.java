package in.gov.manipur.rccms.controller;

import in.gov.manipur.rccms.dto.ApiResponse;
import in.gov.manipur.rccms.dto.CertificateStatusResponse;
import in.gov.manipur.rccms.service.CertificateManagementService;
import in.gov.manipur.rccms.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * APIs for officer digital certificate management.
 * Supports fake mode for testing when app.digital-signature.fake-enabled=true.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Officer Certificate", description = "Certificate management for digital signing (fake mode for testing)")
public class OfficerCertificateController {

    private final CertificateManagementService certificateManagementService;
    private final CurrentUserService currentUserService;

    private static final String FEATURE_NOT_IMPLEMENTED = "Digital signature feature is not yet implemented. Set app.digital-signature.fake-enabled=true for testing.";

    @Operation(
            summary = "Upload Officer Certificate",
            description = "Upload officer's DSC (.pfx/.p12). In fake mode accepts any file for testing."
    )
    @PostMapping("/api/admin/officers/{officerId}/certificate")
    public ResponseEntity<ApiResponse<?>> uploadCertificate(
            @PathVariable Long officerId,
            @RequestParam("certificateFile") MultipartFile certificateFile,
            @RequestParam(value = "password", required = false) String password) {
        log.info("Upload certificate request: officerId={}", officerId);

        if (!certificateManagementService.isFakeEnabled()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ApiResponse.error(FEATURE_NOT_IMPLEMENTED));
        }

        try {
            certificateManagementService.storeCertificate(officerId, certificateFile, password);
            return ResponseEntity.ok(ApiResponse.success("Certificate uploaded successfully (FAKE - for testing)",
                    Map.of(
                            "certificateId", "fake",
                            "issuer", "FAKE - For testing only",
                            "validUntil", "2 years from now",
                            "status", "ACTIVE"
                    )));
        } catch (Exception e) {
            log.error("Upload certificate failed", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @Operation(
            summary = "Get My Certificate Status",
            description = "Get current officer's certificate status. In fake mode returns mock data."
    )
    @GetMapping("/api/officers/me/certificate")
    public ResponseEntity<ApiResponse<?>> getMyCertificateStatus(HttpServletRequest request) {
        Long officerId = currentUserService.getCurrentOfficerId(request);
        if (officerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Officer information not found"));
        }
        log.info("Get certificate status request: officerId={}", officerId);

        if (!certificateManagementService.isFakeEnabled()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ApiResponse.error(FEATURE_NOT_IMPLEMENTED));
        }

        try {
            CertificateStatusResponse status = certificateManagementService.getCertificateStatus(officerId);
            return ResponseEntity.ok(ApiResponse.success("Certificate status retrieved", status));
        } catch (Exception e) {
            log.error("Get certificate status failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
