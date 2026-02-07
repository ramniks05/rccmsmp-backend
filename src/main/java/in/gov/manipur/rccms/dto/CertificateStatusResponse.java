package in.gov.manipur.rccms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for officer certificate status.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificateStatusResponse {

    private Boolean hasCertificate;
    private String certificateType;
    private LocalDateTime validUntil;
    private String status;  // ACTIVE, EXPIRED, INACTIVE
}
