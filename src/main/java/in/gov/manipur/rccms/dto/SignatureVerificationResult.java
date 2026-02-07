package in.gov.manipur.rccms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Result of digital signature verification.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignatureVerificationResult {

    private Boolean isValid;
    private String signedBy;
    private LocalDateTime signedAt;
    private Boolean certificateValid;
    private String certificateExpiry;
    private Boolean documentTampered;
}
