package in.gov.manipur.rccms.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import in.gov.manipur.rccms.entity.FormSectionSchema;
import lombok.Data;

import java.util.List;

@Data
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormSectionSchemaDTO {
    private Long formSectionSchemaId;
    private String formSectionSchemaName;
    private Integer displayOrder;
    private String schemaCode;
    private Boolean isActive;
    private List<FormFieldDefinitionDTO> fields;

    public FormSectionSchemaDTO() {

    }

    public FormSectionSchemaDTO(FormSectionSchema entity) {
        this.formSectionSchemaId = entity != null ? entity.getId() : null;
        this.formSectionSchemaName = entity != null ? entity.getFormSectionSchemaName() : null;
        this.displayOrder = entity != null ? entity.getDisplayOrder() : null;
        this.isActive = entity.getIsActive();
        this.schemaCode=entity.getSchemaCode();
    }
}
