package in.gov.manipur.rccms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Login Request
 * Username can be mobile number or email ID
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Username (mobile number or email) is mandatory")
    @Size(min = 1, max = 100, message = "Username must be between 1 and 100 characters")
    private String username; // Can be mobile number or email

    @NotBlank(message = "Password is mandatory")
    @Size(min = 1, max = 100, message = "Password must be between 1 and 100 characters")
    private String password;
}

