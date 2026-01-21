package in.gov.manipur.rccms.dto;

import lombok.Data;

import java.util.List;

@Data
public class CategoryDTO {
    private Long categoryId;
    private String categoryName;
    private Integer displayOrder;
    private List<FormFieldDefinitionDTO> fields;
}
