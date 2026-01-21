package in.gov.manipur.rccms.dto;

import lombok.Data;

import java.util.List;

@Data
public class CategoryFieldResponseDTO {
    private List<CategoryDTO> categories;
}
