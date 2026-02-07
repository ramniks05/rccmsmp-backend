package in.gov.manipur.rccms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Metadata for PDF document generation (title, case info, officer, court).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentMetadata {

    private String title;
    private String caseNumber;
    private String officerName;
    private String courtName;
    private String documentType;
    private LocalDate currentDate;
}
