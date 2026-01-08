package in.gov.manipur.rccms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for Citizen Response
 * Used for sending citizen data to frontend (excludes sensitive encrypted data)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CitizenResponse {

    private Long id;
    private String fullName;
    private String mobileNumber;
    private String emailId;
    private String districtCode;
    private String address;
    private Boolean isActive;
    private LocalDate registrationDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Note: Aadhaar number is not included in response for security
}

