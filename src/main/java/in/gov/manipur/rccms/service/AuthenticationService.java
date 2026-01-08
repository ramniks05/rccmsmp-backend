package in.gov.manipur.rccms.service;

import in.gov.manipur.rccms.dto.CitizenResponse;
import in.gov.manipur.rccms.dto.LoginRequest;
import in.gov.manipur.rccms.dto.LoginResponse;
import in.gov.manipur.rccms.entity.Citizen;
import in.gov.manipur.rccms.exception.InvalidCredentialsException;
import in.gov.manipur.rccms.repository.CitizenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Authentication Service
 * Handles citizen login with mobile number or email
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthenticationService {

    private final CitizenRepository citizenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // Pattern to check if input is a mobile number (10 digits starting with 6-9)
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^[6-9]\\d{9}$");

    /**
     * Authenticate citizen and generate JWT token
     * @param request login request with username (mobile/email) and password
     * @return LoginResponse with JWT token and citizen data
     * @throws InvalidCredentialsException if credentials are invalid
     */
    public LoginResponse login(LoginRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Login request cannot be null");
        }
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        
        log.info("Login attempt for username: {}", maskUsername(request.getUsername()));

        // Find citizen by mobile number or email
        Optional<Citizen> citizenOpt = findCitizenByUsername(request.getUsername().trim());

        if (citizenOpt.isEmpty()) {
            log.warn("Login failed: Citizen not found with username: {}", maskUsername(request.getUsername()));
            throw new InvalidCredentialsException("Invalid username or password");
        }

        Citizen citizen = citizenOpt.get();

        // Check if citizen is active
        if (!citizen.getIsActive()) {
            log.warn("Login failed: Citizen account is inactive for ID: {}", citizen.getId());
            throw new InvalidCredentialsException("Account is inactive. Please contact support.");
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), citizen.getPassword())) {
            log.warn("Login failed: Invalid password for citizen ID: {}", citizen.getId());
            throw new InvalidCredentialsException("Invalid username or password");
        }

        // Generate JWT token
        String token = jwtService.generateToken(citizen.getId(), citizen.getMobileNumber());
        log.info("Login successful for citizen ID: {}", citizen.getId());

        // Map citizen to response DTO
        CitizenResponse citizenResponse = mapToResponse(citizen);

        // Build login response
        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .citizen(citizenResponse)
                .message("Login successful")
                .build();
    }

    /**
     * Find citizen by username (mobile number or email)
     * @param username mobile number or email
     * @return Optional Citizen
     */
    private Optional<Citizen> findCitizenByUsername(String username) {
        String trimmedUsername = username.trim();

        // Check if it's a mobile number
        if (MOBILE_PATTERN.matcher(trimmedUsername).matches()) {
            log.debug("Searching by mobile number: {}", maskUsername(trimmedUsername));
            return citizenRepository.findByMobileNumber(trimmedUsername);
        }

        // Otherwise, treat as email
        log.debug("Searching by email: {}", maskEmail(trimmedUsername));
        return citizenRepository.findByEmailId(trimmedUsername.toLowerCase());
    }

    /**
     * Mask username for logging (privacy)
     */
    private String maskUsername(String username) {
        if (username == null || username.length() <= 4) {
            return "****";
        }
        return username.substring(0, 2) + "****" + username.substring(username.length() - 2);
    }

    /**
     * Mask email for logging (privacy)
     */
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "****";
        }
        String[] parts = email.split("@");
        if (parts[0].length() <= 2) {
            return "****@" + parts[1];
        }
        return parts[0].substring(0, 2) + "****@" + parts[1];
    }

    /**
     * Map Citizen entity to CitizenResponse DTO
     * @param citizen the Citizen entity
     * @return CitizenResponse DTO
     */
    private CitizenResponse mapToResponse(Citizen citizen) {
        return CitizenResponse.builder()
                .id(citizen.getId())
                .fullName(citizen.getFullName())
                .mobileNumber(citizen.getMobileNumber())
                .emailId(citizen.getEmailId())
                .districtCode(citizen.getDistrictCode())
                .address(citizen.getAddress())
                .isActive(citizen.getIsActive())
                .registrationDate(citizen.getRegistrationDate())
                .createdAt(citizen.getCreatedAt())
                .updatedAt(citizen.getUpdatedAt())
                .build();
    }
}

