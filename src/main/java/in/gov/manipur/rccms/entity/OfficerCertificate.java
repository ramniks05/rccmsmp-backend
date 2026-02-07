package in.gov.manipur.rccms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Officer's digital certificate for document signing (DSC, Aadhaar-based, etc.).
 * Scaffold entity - full implementation pending.
 */
@Entity
@Table(name = "officer_certificates", indexes = {
        @Index(name = "idx_officer_cert_officer", columnList = "officer_id"),
        @Index(name = "idx_officer_cert_active", columnList = "officer_id, is_active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfficerCertificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "officer_id", nullable = false)
    private Long officerId;

    @Column(name = "certificate_type", nullable = false, length = 50)
    private String certificateType; // DSC, AADHAAR, BIOMETRIC

    @Lob
    @Column(name = "certificate_data", columnDefinition = "BYTEA")
    private byte[] certificateData; // Encrypted .pfx/.p12

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(name = "issuer", length = 500)
    private String issuer;

    @Column(name = "subject", length = 500)
    private String subject;

    @Column(name = "serial_number", length = 255)
    private String serialNumber;

    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    @Column(name = "valid_until", nullable = false)
    private LocalDateTime validUntil;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
