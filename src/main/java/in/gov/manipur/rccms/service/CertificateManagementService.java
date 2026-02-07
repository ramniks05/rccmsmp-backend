package in.gov.manipur.rccms.service;

import in.gov.manipur.rccms.dto.CertificateStatusResponse;
import in.gov.manipur.rccms.entity.OfficerCertificate;
import in.gov.manipur.rccms.repository.OfficerCertificateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * Service for managing officer digital certificates.
 * Supports fake mode for testing - accepts any file and stores mock record.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CertificateManagementService {

    @Value("${app.digital-signature.fake-enabled:false}")
    private boolean fakeEnabled;

    private final OfficerCertificateRepository certificateRepository;

    /**
     * Store officer's digital certificate (DSC .pfx/.p12).
     * When fake-enabled: stores mock record for testing.
     */
    @Transactional
    public void storeCertificate(Long officerId, MultipartFile file, String password) {
        if (fakeEnabled) {
            storeCertificateFake(officerId, file, password);
            return;
        }
        throw new UnsupportedOperationException(
                "Certificate management is not yet implemented. Set app.digital-signature.fake-enabled=true for testing.");
    }

    /**
     * Fake certificate upload - stores minimal record for testing.
     */
    private void storeCertificateFake(Long officerId, MultipartFile file, String password) {
        log.info("FAKE certificate upload: officerId={}", officerId);

        // Deactivate existing certificates for this officer
        certificateRepository.findByOfficerIdAndIsActiveTrue(officerId)
                .ifPresent(existing -> {
                    existing.setIsActive(false);
                    certificateRepository.save(existing);
                });

        OfficerCertificate cert = new OfficerCertificate();
        cert.setOfficerId(officerId);
        cert.setCertificateType("FAKE");
        cert.setCertificateData(new byte[0]); // No actual cert stored in fake mode
        cert.setPasswordHash(null);
        cert.setIssuer("FAKE - For testing only");
        cert.setSubject("Officer " + officerId);
        cert.setSerialNumber("FAKE-" + System.currentTimeMillis());
        cert.setValidFrom(LocalDateTime.now());
        cert.setValidUntil(LocalDateTime.now().plusYears(2));
        cert.setIsActive(true);
        certificateRepository.save(cert);
    }

    /**
     * Get officer's certificate status.
     * When fake-enabled: returns status from mock record if exists.
     */
    public CertificateStatusResponse getCertificateStatus(Long officerId) {
        if (fakeEnabled) {
            return getCertificateStatusFake(officerId);
        }
        throw new UnsupportedOperationException(
                "Certificate management is not yet implemented. Set app.digital-signature.fake-enabled=true for testing.");
    }

    private CertificateStatusResponse getCertificateStatusFake(Long officerId) {
        return certificateRepository.findByOfficerIdAndIsActiveTrue(officerId)
                .map(cert -> CertificateStatusResponse.builder()
                        .hasCertificate(true)
                        .certificateType("FAKE")
                        .validUntil(cert.getValidUntil())
                        .status("ACTIVE")
                        .build())
                .orElse(CertificateStatusResponse.builder()
                        .hasCertificate(false)
                        .certificateType(null)
                        .validUntil(null)
                        .status("NO_CERTIFICATE")
                        .build());
    }

    public boolean isFakeEnabled() {
        return fakeEnabled;
    }
}
