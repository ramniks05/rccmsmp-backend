package in.gov.manipur.rccms.repository;

import in.gov.manipur.rccms.entity.Citizen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Citizen Repository interface
 * Provides data access operations for Citizen entities
 */
@Repository
public interface CitizenRepository extends JpaRepository<Citizen, Long> {

    /**
     * Find citizen by mobile number
     * @param mobileNumber the mobile number to search for
     * @return Optional Citizen if found
     */
    Optional<Citizen> findByMobileNumber(String mobileNumber);

    /**
     * Check if citizen exists by mobile number
     * @param mobileNumber the mobile number to check
     * @return true if exists, false otherwise
     */
    boolean existsByMobileNumber(String mobileNumber);

    /**
     * Find citizen by email ID (if provided)
     * @param emailId the email ID to search for
     * @return Optional Citizen if found
     */
    Optional<Citizen> findByEmailId(String emailId);

    /**
     * Find active citizen by mobile number
     * @param mobileNumber the mobile number to search for
     * @return Optional Citizen if found and active
     */
    Optional<Citizen> findByMobileNumberAndIsActiveTrue(String mobileNumber);
}

