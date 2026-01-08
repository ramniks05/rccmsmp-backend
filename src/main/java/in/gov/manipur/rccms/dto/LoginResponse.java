package in.gov.manipur.rccms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Login Response
 * Contains JWT token and citizen information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token; // JWT token
    @Builder.Default
    private String tokenType = "Bearer";
    private CitizenResponse citizen;
    private String message;
}

