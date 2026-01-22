package in.gov.manipur.rccms.repository;

import in.gov.manipur.rccms.entity.FormSectionSchema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormSectionSchemaRepository extends JpaRepository<FormSectionSchema, Long> {
    List<FormSectionSchema> findByCaseTypeId(Long caseTypeId);

    boolean existsBySchemaCode(String code);
    boolean existsByFormSectionSchemaName(String formSectionSchemaName);
}
