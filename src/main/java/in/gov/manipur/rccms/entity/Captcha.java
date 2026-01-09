package in.gov.manipur.rccms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * CAPTCHA Entity
 * Stores CAPTCHA codes for validation
 */
@Entity
@Table(name = "captchas", indexes = {
        @Index(name = "idx_captcha_id", columnList = "captcha_id")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Captcha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "captcha_id", nullable = false, unique = true, length = 36)
    private String captchaId; // UUID

    @Column(name = "captcha_text", nullable = false, length = 10)
    private String captchaText; // 6-8 alphanumeric characters

    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false;

    @Column(name = "ip_address", length = 45)
    private String ipAddress; // For tracking

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @PrePersist
    protected void onCreate() {
        if (captchaId == null) {
            captchaId = UUID.randomUUID().toString();
        }
        if (expiresAt == null) {
            expiresAt = LocalDateTime.now().plusMinutes(10); // CAPTCHA expires in 10 minutes
        }
        if (isUsed == null) {
            isUsed = false;
        }
    }
}

