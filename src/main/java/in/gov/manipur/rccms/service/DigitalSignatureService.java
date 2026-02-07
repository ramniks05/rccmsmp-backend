package in.gov.manipur.rccms.service;

import in.gov.manipur.rccms.dto.SignDocumentRequest;
import in.gov.manipur.rccms.dto.SignatureVerificationResult;
import in.gov.manipur.rccms.dto.SignedDocumentResponse;
import in.gov.manipur.rccms.entity.*;
import in.gov.manipur.rccms.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Service for digitally signing court documents (Notice, Ordersheet, Judgement).
 * Supports fake mode for testing - simulates signing without real DSC/OTP/Aadhaar.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DigitalSignatureService {

    @Value("${app.digital-signature.enabled:false}")
    private boolean digitalSignatureEnabled;

    @Value("${app.digital-signature.fake-enabled:false}")
    private boolean fakeEnabled;

    @Value("${app.digital-signature.storage-path:./data/signed-documents/}")
    private String storagePath;

    private final CaseDocumentRepository documentRepository;
    private final DigitalSignatureRepository signatureRepository;
    private final SignedDocumentRepository signedDocumentRepository;
    private final OfficerRepository officerRepository;

    /**
     * Sign document with officer's digital signature.
     * When fake-enabled: simulates signing for testing (no real DSC/OTP/Aadhaar).
     */
    @Transactional
    public SignedDocumentResponse signDocument(Long caseId, ModuleType documentType, Long officerId,
                                               SignDocumentRequest request) {
        if (fakeEnabled) {
            return signDocumentFake(caseId, documentType, officerId, request);
        }
        if (!digitalSignatureEnabled) {
            throw new UnsupportedOperationException(
                    "Digital signature feature is not yet implemented. Set app.digital-signature.fake-enabled=true for testing.");
        }
        throw new UnsupportedOperationException(
                "Real digital signature not yet implemented. Use fake-enabled for testing.");
    }

    /**
     * Fake signing - creates records and stores HTML as file for testing.
     */
    private SignedDocumentResponse signDocumentFake(Long caseId, ModuleType documentType, Long officerId,
                                                    SignDocumentRequest request) {
        log.info("FAKE signing document: caseId={}, moduleType={}, officerId={}", caseId, documentType, officerId);

        CaseDocument doc = documentRepository.findTopByCaseIdAndModuleTypeOrderByUpdatedAtDesc(caseId, documentType)
                .orElseThrow(() -> new RuntimeException("Document not found for case " + caseId + " and type " + documentType));

        String content = request.getDocumentContent() != null && !request.getDocumentContent().isEmpty()
                ? request.getDocumentContent()
                : (doc.getContentHtml() != null ? doc.getContentHtml() : "<p>No content</p>");

        // Create minimal PDF-like content (HTML wrapped for testing - opens in browser)
        String wrappedContent = wrapAsFakePdf(content);

        // Create DigitalSignature record
        DigitalSignature signature = new DigitalSignature();
        signature.setDocumentId(doc.getId());
        signature.setDocumentType(documentType.name());
        signature.setCaseId(caseId);
        signature.setOfficerId(officerId);
        signature.setSignatureMethod(request.getSignatureMethod() != null ? request.getSignatureMethod() : "FAKE");
        signature.setSignatureData("FAKE".getBytes(StandardCharsets.UTF_8));
        signature.setSignatureTimestamp(LocalDateTime.now());
        signature.setReason(request.getReason() != null ? request.getReason() : "FAKE - For testing only");
        signature.setLocation(request.getLocation() != null ? request.getLocation() : "Testing");
        signature.setVerificationStatus("VALID");
        signature = signatureRepository.save(signature);

        // Save file
        String fileName = documentType + "_Case" + caseId + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";
        Path dir = Paths.get(storagePath).resolve(String.valueOf(caseId));
        try {
            Files.createDirectories(dir);
            Path filePath = dir.resolve(fileName);
            Files.writeString(filePath, wrappedContent, StandardCharsets.UTF_8);
            String relativePath = caseId + "/" + fileName;

            // Create SignedDocument record
            SignedDocument signedDoc = new SignedDocument();
            signedDoc.setOriginalDocumentId(doc.getId());
            signedDoc.setDocumentType(documentType.name());
            signedDoc.setCaseId(caseId);
            signedDoc.setSignedPdfPath(relativePath);
            signedDoc.setFileName(fileName);
            signedDoc.setFileSize((long) wrappedContent.getBytes(StandardCharsets.UTF_8).length);
            signedDoc.setPdfHash(sha256(wrappedContent));
            signedDoc.setSignatureId(signature.getId());
            signedDoc.setStatus("SIGNED");
            signedDoc.setCreatedBy(officerId);
            signedDoc = signedDocumentRepository.save(signedDoc);

            // Update CaseDocument
            doc.setStatus(DocumentStatus.SIGNED);
            doc.setSignedByOfficerId(officerId);
            doc.setSignedAt(LocalDateTime.now());
            doc.setSignatureId(signature.getId());
            doc.setSignedDocumentId(signedDoc.getId());
            documentRepository.save(doc);

            return SignedDocumentResponse.builder()
                    .signedDocumentId(signedDoc.getId())
                    .signatureId(signature.getId())
                    .pdfUrl("/api/documents/signed/" + signedDoc.getId() + "/download")
                    .signatureTimestamp(signature.getSignatureTimestamp())
                    .status("SIGNED")
                    .fileName(fileName)
                    .fileSize(signedDoc.getFileSize())
                    .build();
        } catch (Exception e) {
            log.error("Failed to save fake signed document", e);
            throw new RuntimeException("Failed to save signed document: " + e.getMessage());
        }
    }

    private String wrapAsFakePdf(String content) {
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Signed Document (FAKE - Testing)</title>" +
                "<style>body{font-family:serif;margin:2cm;} .watermark{color:#ccc;font-size:10px;margin-top:20px;}</style></head>" +
                "<body>" + content + "<div class='watermark'>[FAKE SIGNATURE - For testing only. Replace with real DSC/OTP/Aadhaar implementation.]</div></body></html>";
    }

    private String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return "fake-hash";
        }
    }

    /**
     * Verify digital signature on a signed document.
     * When fake-enabled: returns mock verification result.
     */
    public SignatureVerificationResult verifySignature(Long signedDocumentId) {
        if (fakeEnabled) {
            return verifySignatureFake(signedDocumentId);
        }
        throw new UnsupportedOperationException("Signature verification not yet implemented. Use fake-enabled for testing.");
    }

    private SignatureVerificationResult verifySignatureFake(Long signedDocumentId) {
        SignedDocument signedDoc = signedDocumentRepository.findById(signedDocumentId)
                .orElseThrow(() -> new RuntimeException("Signed document not found: " + signedDocumentId));

        String signedBy = "Unknown";
        Optional<Officer> officer = officerRepository.findById(signedDoc.getCreatedBy());
        if (officer.isPresent()) {
            signedBy = officer.get().getFullName();
        }

        return SignatureVerificationResult.builder()
                .isValid(true)
                .signedBy(signedBy + " [FAKE]")
                .signedAt(signedDoc.getCreatedAt())
                .certificateValid(true)
                .certificateExpiry("9999-12-31")
                .documentTampered(false)
                .build();
    }

    /**
     * Get signed document file for download.
     * When fake-enabled: returns stored HTML/PDF file.
     */
    public Resource getSignedDocumentFile(Long signedDocumentId) {
        if (!fakeEnabled && !digitalSignatureEnabled) {
            throw new UnsupportedOperationException("Digital signature feature is not implemented.");
        }

        SignedDocument signedDoc = signedDocumentRepository.findById(signedDocumentId)
                .orElseThrow(() -> new RuntimeException("Signed document not found: " + signedDocumentId));

        try {
            Path filePath = Paths.get(storagePath).resolve(signedDoc.getSignedPdfPath());
            byte[] bytes = Files.readAllBytes(filePath);
            return new ByteArrayResource(bytes);
        } catch (Exception e) {
            log.error("Failed to read signed document file", e);
            throw new RuntimeException("Failed to load signed document: " + e.getMessage());
        }
    }

    /**
     * Get filename for signed document download.
     */
    public String getSignedDocumentFileName(Long signedDocumentId) {
        SignedDocument signedDoc = signedDocumentRepository.findById(signedDocumentId)
                .orElseThrow(() -> new RuntimeException("Signed document not found: " + signedDocumentId));
        return signedDoc.getFileName();
    }

    /**
     * Check if fake mode is enabled (for testing).
     */
    public boolean isFakeEnabled() {
        return fakeEnabled;
    }

    /**
     * Check if digital signature feature is available (enabled or fake).
     */
    public boolean isEnabled() {
        return digitalSignatureEnabled || fakeEnabled;
    }
}
