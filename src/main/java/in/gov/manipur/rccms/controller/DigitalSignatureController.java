package in.gov.manipur.rccms.controller;

import in.gov.manipur.rccms.dto.ApiResponse;
import in.gov.manipur.rccms.dto.SignDocumentRequest;
import in.gov.manipur.rccms.dto.SignatureVerificationResult;
import in.gov.manipur.rccms.dto.SignedDocumentResponse;
import in.gov.manipur.rccms.entity.ModuleType;
import in.gov.manipur.rccms.service.CurrentUserService;
import in.gov.manipur.rccms.service.DigitalSignatureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * APIs for digital signature on court documents (Notice, Ordersheet, Judgement).
 * Supports fake mode for testing when app.digital-signature.fake-enabled=true.
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Digital Signature", description = "Document signing and verification (fake mode for testing)")
public class DigitalSignatureController {

    private final DigitalSignatureService digitalSignatureService;
    private final CurrentUserService currentUserService;

    private static final String FEATURE_NOT_IMPLEMENTED = "Digital signature feature is not yet implemented. Set app.digital-signature.fake-enabled=true for testing.";

    @Operation(
            summary = "Sign Document",
            description = "Sign document with digital signature. Uses fake mode when fake-enabled=true."
    )
    @PostMapping("/cases/{caseId}/documents/{moduleType}/sign")
    public ResponseEntity<ApiResponse<?>> signDocument(
            @PathVariable Long caseId,
            @PathVariable ModuleType moduleType,
            @RequestBody SignDocumentRequest request,
            HttpServletRequest httpRequest) {
        log.info("Sign document request: caseId={}, moduleType={}", caseId, moduleType);

        if (!digitalSignatureService.isEnabled()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ApiResponse.error(FEATURE_NOT_IMPLEMENTED));
        }

        Long officerId = currentUserService.getCurrentOfficerId(httpRequest);
        if (officerId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Officer information not found"));
        }

        try {
            SignedDocumentResponse response = digitalSignatureService.signDocument(
                    caseId, moduleType, officerId, request);
            return ResponseEntity.ok(ApiResponse.success("Document signed successfully", response));
        } catch (Exception e) {
            log.error("Sign document failed", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @Operation(
            summary = "Download Signed PDF",
            description = "Download signed PDF document. Works in fake mode for testing."
    )
    @GetMapping("/documents/signed/{signedDocumentId}/download")
    public ResponseEntity<?> downloadSignedDocument(@PathVariable Long signedDocumentId) {
        log.info("Download signed document request: signedDocumentId={}", signedDocumentId);

        if (!digitalSignatureService.isEnabled()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ApiResponse.error(FEATURE_NOT_IMPLEMENTED));
        }

        try {
            Resource resource = digitalSignatureService.getSignedDocumentFile(signedDocumentId);
            String fileName = digitalSignatureService.getSignedDocumentFileName(signedDocumentId);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            log.error("Download signed document failed", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @Operation(
            summary = "Verify Signature",
            description = "Verify digital signature on signed document. Works in fake mode for testing."
    )
    @GetMapping("/documents/signed/{signedDocumentId}/verify")
    public ResponseEntity<ApiResponse<?>> verifySignature(@PathVariable Long signedDocumentId) {
        log.info("Verify signature request: signedDocumentId={}", signedDocumentId);

        if (!digitalSignatureService.isEnabled()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ApiResponse.error(FEATURE_NOT_IMPLEMENTED));
        }

        try {
            SignatureVerificationResult result = digitalSignatureService.verifySignature(signedDocumentId);
            return ResponseEntity.ok(ApiResponse.success("Verification complete", result));
        } catch (Exception e) {
            log.error("Verify signature failed", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
