package in.gov.manipur.rccms.repository;

import in.gov.manipur.rccms.entity.SignedDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SignedDocumentRepository extends JpaRepository<SignedDocument, Long> {

    List<SignedDocument> findByCaseIdAndDocumentType(Long caseId, String documentType);

    List<SignedDocument> findByOriginalDocumentId(Long originalDocumentId);
}
