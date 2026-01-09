package in.gov.manipur.rccms.service;

import in.gov.manipur.rccms.dto.AuthResponseDTO;
import in.gov.manipur.rccms.dto.LoginRequestDTO;
import in.gov.manipur.rccms.dto.OtpVerificationDTO;
import in.gov.manipur.rccms.entity.User;
import in.gov.manipur.rccms.exception.InvalidCredentialsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authentication Service
 * Handles user authentication (password and OTP login)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserService userService;
    private final OtpService otpService;
    private final CaptchaService captchaService;
    private final JwtService jwtService;

    /**
     * Login with password
     */
    public AuthResponseDTO loginWithPassword(LoginRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Login request cannot be null");
        }

        // Validate CAPTCHA
        boolean isValidCaptcha = captchaService.validateCaptcha(request.getCaptchaId(), request.getCaptcha());
        if (!isValidCaptcha) {
            log.warn("Login failed: Invalid CAPTCHA");
            throw new InvalidCredentialsException("Invalid CAPTCHA");
        }

        // Mark CAPTCHA as used
        captchaService.markCaptchaAsUsed(request.getCaptchaId(), request.getCaptcha());

        // Find and verify user
        User user = userService.findByEmailOrMobile(request.getUsername().trim());
        
        // Verify user type matches
        if (!user.getUserType().equals(request.getUserType())) {
            log.warn("Login failed: User type mismatch for user ID: {}", user.getId());
            throw new InvalidCredentialsException("Invalid user type");
        }

        // Check if account is active
        if (!user.getIsActive()) {
            log.warn("Login failed: Account is inactive for user ID: {}", user.getId());
            throw new InvalidCredentialsException("Account is not active. Please verify your mobile number.");
        }

        // Verify password
        if (!userService.verifyUserCredentials(request.getUsername().trim(), request.getPassword()).equals(user)) {
            log.warn("Login failed: Invalid password for user ID: {}", user.getId());
            throw new InvalidCredentialsException("Invalid username or password");
        }

        // Generate tokens
        String accessToken = jwtService.generateToken(user.getId(), user.getEmail(), user.getUserType().name());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        log.info("Password login successful for user ID: {}", user.getId());

        return AuthResponseDTO.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .userType(user.getUserType())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .expiresIn(3600) // 1 hour in seconds
                .build();
    }

    /**
     * Login with OTP
     */
    @Transactional
    public AuthResponseDTO loginWithOtp(OtpVerificationDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("OTP verification request cannot be null");
        }

        // Validate CAPTCHA
        boolean isValidCaptcha = captchaService.validateCaptcha(request.getCaptchaId(), request.getCaptcha());
        if (!isValidCaptcha) {
            log.warn("OTP login failed: Invalid CAPTCHA");
            throw new InvalidCredentialsException("Invalid CAPTCHA");
        }

        // Mark CAPTCHA as used
        captchaService.markCaptchaAsUsed(request.getCaptchaId(), request.getCaptcha());

        // Verify OTP
        boolean isValidOtp = otpService.verifyOtp(
                request.getMobileNumber().trim(), 
                request.getOtp().trim(), 
                request.getUserType()
        );
        
        if (!isValidOtp) {
            log.warn("OTP login failed: Invalid OTP for mobile: {}", maskMobile(request.getMobileNumber()));
            throw new InvalidCredentialsException("Invalid or expired OTP");
        }

        // Find user
        User user = userService.findByEmailOrMobile(request.getMobileNumber().trim());
        
        // Verify user type matches
        if (!user.getUserType().equals(request.getUserType())) {
            log.warn("OTP login failed: User type mismatch for user ID: {}", user.getId());
            throw new InvalidCredentialsException("Invalid user type");
        }

        // Check if account is active
        if (!user.getIsActive()) {
            log.warn("OTP login failed: Account is inactive for user ID: {}", user.getId());
            throw new InvalidCredentialsException("Account is not active. Please contact support.");
        }

        // Mark OTP as used
        otpService.markOtpAsUsed(request.getMobileNumber().trim(), request.getOtp().trim(), request.getUserType());

        // Generate tokens
        String accessToken = jwtService.generateToken(user.getId(), user.getEmail(), user.getUserType().name());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        log.info("OTP login successful for user ID: {}", user.getId());

        return AuthResponseDTO.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .userType(user.getUserType())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .expiresIn(3600) // 1 hour in seconds
                .build();
    }

    /**
     * Refresh access token using refresh token
     */
    public AuthResponseDTO refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            throw new IllegalArgumentException("Refresh token cannot be null or empty");
        }

        // Validate refresh token
        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw new InvalidCredentialsException("Invalid or expired refresh token");
        }

        // Extract user info from refresh token
        Long userId = jwtService.extractUserId(refreshToken);
        String username = jwtService.extractUsername(refreshToken);
        
        User user = userService.findById(userId);

        // Generate new access token
        String newAccessToken = jwtService.generateToken(user.getId(), username, user.getUserType().name());

        log.info("Token refreshed for user ID: {}", userId);

        return AuthResponseDTO.builder()
                .token(newAccessToken)
                .refreshToken(refreshToken) // Return same refresh token
                .userId(user.getId())
                .userType(user.getUserType())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .expiresIn(3600)
                .build();
    }

    /**
     * Mask mobile for logging
     */
    private String maskMobile(String mobile) {
        if (mobile == null || mobile.length() != 10) {
            return "****";
        }
        return mobile.substring(0, 2) + "****" + mobile.substring(8);
    }
}

