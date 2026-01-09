package in.gov.manipur.rccms.repository;

import in.gov.manipur.rccms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User Repository interface
 * Provides data access operations for User entities
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by mobile number
     */
    Optional<User> findByMobileNumber(String mobileNumber);

    /**
     * Find user by Aadhar number
     */
    Optional<User> findByAadharNumber(String aadharNumber);

    /**
     * Find user by email or mobile number
     */
    Optional<User> findByEmailOrMobileNumber(String email, String mobileNumber);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Check if mobile number exists
     */
    boolean existsByMobileNumber(String mobileNumber);

    /**
     * Check if Aadhar number exists
     */
    boolean existsByAadharNumber(String aadharNumber);

    /**
     * Find active user by email or mobile number
     */
    Optional<User> findByEmailOrMobileNumberAndIsActiveTrue(String email, String mobileNumber);
}

