package in.gov.manipur.rccms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormSectionSchemaFieldResponseDTO {
    private List<FormSectionSchemaDTO> formSectionSchema;
}
