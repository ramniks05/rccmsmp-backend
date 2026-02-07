package in.gov.manipur.rccms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Digital signature record for signed documents.
 * Scaffold entity - full implementation pending.
 */
@Entity
@Table(name = "digital_signatures", indexes = {
        @Index(name = "idx_digital_sig_document", columnList = "document_id, document_type"),
        @Index(name = "idx_digital_sig_case", columnList = "case_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DigitalSignature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_id", nullable = false)
    private Long documentId;

    @Column(name = "document_type", nullable = false, length = 50)
    private String documentType; // NOTICE, ORDERSHEET, JUDGEMENT

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "officer_id", nullable = false)
    private Long officerId;

    @Column(name = "certificate_id")
    private Long certificateId;

    @Column(name = "signature_method", nullable = false, length = 50)
    private String signatureMethod; // DSC, AADHAAR_OTP, BIOMETRIC

    @Lob
    @Column(name = "signature_data", columnDefinition = "BYTEA")
    private byte[] signatureData;

    @Column(name = "signature_timestamp", nullable = false)
    private LocalDateTime signatureTimestamp;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "verification_status", length = 50)
    private String verificationStatus = "PENDING";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
