package in.gov.manipur.rccms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for signed document operation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignedDocumentResponse {

    private Long signedDocumentId;
    private Long signatureId;
    private String pdfUrl;
    private LocalDateTime signatureTimestamp;
    private String status;
    private String fileName;
    private Long fileSize;
}
