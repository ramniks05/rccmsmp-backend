package in.gov.manipur.rccms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Citizen Registration Request
 * Used for receiving registration data from frontend
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CitizenRegistrationRequest {

    @NotBlank(message = "Full name is mandatory")
    @Size(max = 200, message = "Full name must not exceed 200 characters")
    private String fullName;

    @NotBlank(message = "Mobile number is mandatory")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Mobile number must be a valid 10-digit Indian mobile number")
    private String mobileNumber;

    @Email(message = "Email must be a valid email address")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String emailId;

    @NotBlank(message = "District code is mandatory")
    @Size(max = 10, message = "District code must not exceed 10 characters")
    private String districtCode;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    private String aadhaarNumber; // Optional, no validation

    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;
}

