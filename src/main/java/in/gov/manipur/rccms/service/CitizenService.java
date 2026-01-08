package in.gov.manipur.rccms.service;

import in.gov.manipur.rccms.dto.CitizenRegistrationRequest;
import in.gov.manipur.rccms.dto.CitizenResponse;
import in.gov.manipur.rccms.entity.Citizen;
import in.gov.manipur.rccms.exception.DuplicateMobileNumberException;
import in.gov.manipur.rccms.repository.CitizenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Service class for Citizen business logic
 * Handles registration, validation, and data transformation
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CitizenService {

    private final CitizenRepository citizenRepository;
    private final EncryptionService encryptionService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Register a new citizen
     * @param request the registration request DTO
     * @return CitizenResponse with registered citizen data
     * @throws DuplicateMobileNumberException if mobile number already exists
     */
    public CitizenResponse registerCitizen(CitizenRegistrationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Registration request cannot be null");
        }
        if (request.getMobileNumber() == null || request.getMobileNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Mobile number is required");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        
        log.info("Registering new citizen with mobile number: {}", request.getMobileNumber());

        // Check if mobile number already exists
        if (citizenRepository.existsByMobileNumber(request.getMobileNumber().trim())) {
            log.warn("Registration failed: Mobile number {} already exists", request.getMobileNumber());
            throw new DuplicateMobileNumberException(
                    "Mobile number " + request.getMobileNumber() + " is already registered"
            );
        }

        // Create new Citizen entity
        Citizen citizen = new Citizen();
        citizen.setFullName(request.getFullName().trim());
        citizen.setMobileNumber(request.getMobileNumber().trim());
        citizen.setDistrictCode(request.getDistrictCode().trim().toUpperCase());
        citizen.setIsActive(true);
        citizen.setRegistrationDate(LocalDate.now());

        // Set optional fields
        if (request.getEmailId() != null && !request.getEmailId().trim().isEmpty()) {
            citizen.setEmailId(request.getEmailId().trim().toLowerCase());
        }

        if (request.getAddress() != null && !request.getAddress().trim().isEmpty()) {
            citizen.setAddress(request.getAddress().trim());
        }

        // Encrypt Aadhaar number if provided
        if (request.getAadhaarNumber() != null && !request.getAadhaarNumber().trim().isEmpty()) {
            String encryptedAadhaar = encryptionService.encrypt(request.getAadhaarNumber().trim());
            citizen.setAadhaarNumber(encryptedAadhaar);
            log.debug("Aadhaar number encrypted for citizen with mobile: {}", request.getMobileNumber());
        }

        // Hash password with BCrypt
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        citizen.setPassword(hashedPassword);
        log.debug("Password hashed for citizen with mobile: {}", request.getMobileNumber());

        // Save citizen
        Citizen savedCitizen = citizenRepository.save(citizen);
        log.info("Citizen registered successfully with ID: {}", savedCitizen.getId());

        // Convert to response DTO
        return mapToResponse(savedCitizen);
    }

    /**
     * Get citizen by ID
     * @param id the citizen ID
     * @return CitizenResponse
     */
    @Transactional(readOnly = true)
    public CitizenResponse getCitizenById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Citizen ID cannot be null");
        }
        log.debug("Fetching citizen with ID: {}", id);
        Citizen citizen = citizenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Citizen not found with ID: " + id));
        return mapToResponse(citizen);
    }

    /**
     * Get citizen by mobile number
     * @param mobileNumber the mobile number
     * @return CitizenResponse
     */
    @Transactional(readOnly = true)
    public CitizenResponse getCitizenByMobileNumber(String mobileNumber) {
        if (mobileNumber == null || mobileNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Mobile number cannot be null or empty");
        }
        log.debug("Fetching citizen with mobile number: {}", mobileNumber);
        Citizen citizen = citizenRepository.findByMobileNumber(mobileNumber.trim())
                .orElseThrow(() -> new RuntimeException("Citizen not found with mobile number: " + mobileNumber));
        return mapToResponse(citizen);
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

