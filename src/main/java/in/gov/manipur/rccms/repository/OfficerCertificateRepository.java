package in.gov.manipur.rccms.repository;

import in.gov.manipur.rccms.entity.OfficerCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OfficerCertificateRepository extends JpaRepository<OfficerCertificate, Long> {

    Optional<OfficerCertificate> findByOfficerIdAndIsActiveTrue(Long officerId);

    boolean existsByOfficerIdAndIsActiveTrue(Long officerId);
}
