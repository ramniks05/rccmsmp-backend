package in.gov.manipur.rccms.repository;

import in.gov.manipur.rccms.entity.DigitalSignature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DigitalSignatureRepository extends JpaRepository<DigitalSignature, Long> {

    List<DigitalSignature> findByDocumentIdAndDocumentType(Long documentId, String documentType);

    List<DigitalSignature> findByCaseId(Long caseId);
}
