package in.gov.manipur.rccms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Signed PDF document with embedded digital signature.
 * Scaffold entity - full implementation pending.
 */
@Entity
@Table(name = "signed_documents", indexes = {
        @Index(name = "idx_signed_doc_case", columnList = "case_id, document_type")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignedDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_document_id", nullable = false)
    private Long originalDocumentId;

    @Column(name = "document_type", nullable = false, length = 50)
    private String documentType;

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "signed_pdf_path", nullable = false, length = 500)
    private String signedPdfPath;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "pdf_hash", nullable = false, length = 255)
    private String pdfHash;

    @Column(name = "signature_id", nullable = false)
    private Long signatureId;

    @Column(name = "status", length = 50)
    private String status = "SIGNED";

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
