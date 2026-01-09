package in.gov.manipur.rccms.repository;

import in.gov.manipur.rccms.entity.Captcha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * CAPTCHA Repository interface
 * Provides data access operations for CAPTCHA entities
 */
@Repository
public interface CaptchaRepository extends JpaRepository<Captcha, Long> {

    /**
     * Find valid CAPTCHA by ID and text
     */
    @Query("SELECT c FROM Captcha c WHERE c.captchaId = :captchaId " +
           "AND c.captchaText = :captchaText AND c.isUsed = false " +
           "AND c.expiresAt > :now")
    Optional<Captcha> findValidCaptcha(
            @Param("captchaId") String captchaId,
            @Param("captchaText") String captchaText,
            @Param("now") LocalDateTime now);

    /**
     * Mark CAPTCHA as used
     */
    @Modifying
    @Query("UPDATE Captcha c SET c.isUsed = true WHERE c.id = :id")
    void markAsUsed(@Param("id") Long id);

    /**
     * Delete expired CAPTCHAs
     */
    @Modifying
    @Query("DELETE FROM Captcha c WHERE c.expiresAt < :now")
    void deleteExpiredCaptchas(@Param("now") LocalDateTime now);
}

