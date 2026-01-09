package in.gov.manipur.rccms.service;

import in.gov.manipur.rccms.dto.CaptchaDTO;
import in.gov.manipur.rccms.entity.Captcha;
import in.gov.manipur.rccms.repository.CaptchaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

/**
 * CAPTCHA Service
 * Handles CAPTCHA generation and validation
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CaptchaService {

    private final CaptchaRepository captchaRepository;
    private static final String ALPHANUMERIC = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // Excluding confusing characters
    private static final int CAPTCHA_LENGTH = 6;
    private static final Random random = new Random();

    /**
     * Generate CAPTCHA
     * @param ipAddress IP address of the requester (for tracking)
     * @return CaptchaDTO with captchaId and captchaText
     */
    public CaptchaDTO generateCaptcha(String ipAddress) {
        String captchaText = generateCaptchaText();
        Captcha captcha = new Captcha();
        captcha.setCaptchaText(captchaText);
        captcha.setIpAddress(ipAddress);
        captcha.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        captcha.setIsUsed(false);

        Captcha savedCaptcha = captchaRepository.save(captcha);
        log.debug("CAPTCHA generated with ID: {}", savedCaptcha.getCaptchaId());

        return CaptchaDTO.builder()
                .captchaId(savedCaptcha.getCaptchaId())
                .captchaText(savedCaptcha.getCaptchaText())
                .build();
    }

    /**
     * Validate CAPTCHA
     * @param captchaId CAPTCHA ID
     * @param captchaText CAPTCHA text (case-insensitive)
     * @return true if valid, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean validateCaptcha(String captchaId, String captchaText) {
        if (captchaId == null || captchaText == null) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        Optional<Captcha> captchaOpt = captchaRepository.findValidCaptcha(
                captchaId, 
                captchaText.toUpperCase(), // Case-insensitive comparison
                now
        );

        return captchaOpt.isPresent();
    }

    /**
     * Mark CAPTCHA as used after successful validation
     */
    public void markCaptchaAsUsed(String captchaId, String captchaText) {
        LocalDateTime now = LocalDateTime.now();
        Optional<Captcha> captchaOpt = captchaRepository.findValidCaptcha(
                captchaId,
                captchaText.toUpperCase(),
                now
        );

        if (captchaOpt.isPresent()) {
            captchaRepository.markAsUsed(captchaOpt.get().getId());
            log.debug("CAPTCHA marked as used: {}", captchaId);
        }
    }

    /**
     * Generate random alphanumeric CAPTCHA text
     */
    private String generateCaptchaText() {
        StringBuilder captcha = new StringBuilder();
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            captcha.append(ALPHANUMERIC.charAt(random.nextInt(ALPHANUMERIC.length())));
        }
        return captcha.toString();
    }

    /**
     * Clean up expired CAPTCHAs (runs every hour)
     */
    @Scheduled(fixedRate = 3600000) // 1 hour in milliseconds
    @Transactional
    public void cleanupExpiredCaptchas() {
        LocalDateTime now = LocalDateTime.now();
        captchaRepository.deleteExpiredCaptchas(now);
        log.debug("Cleaned up expired CAPTCHAs");
    }
}

