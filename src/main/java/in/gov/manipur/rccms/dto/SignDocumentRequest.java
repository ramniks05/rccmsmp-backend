package in.gov.manipur.rccms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for signing a document with digital signature.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignDocumentRequest {

    private String documentContent;  // HTML content to sign
    private String signatureMethod;  // DSC, AADHAAR_OTP, BIOMETRIC
    private String certificatePassword;  // For DSC method
    private String reason;  // Signature reason
    private String location;  // Signature location
}
