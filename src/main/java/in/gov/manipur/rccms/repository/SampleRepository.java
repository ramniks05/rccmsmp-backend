package in.gov.manipur.rccms.repository;

import in.gov.manipur.rccms.entity.SampleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Sample Repository interface
 * Extends JpaRepository to provide CRUD operations
 * Spring Data JPA automatically implements this interface
 */
@Repository
public interface SampleRepository extends JpaRepository<SampleEntity, Long> {

    /**
     * Find all active sample entities
     * Spring Data JPA automatically implements this method based on method name
     */
    List<SampleEntity> findByIsActiveTrue();

    /**
     * Find sample entity by name
     */
    Optional<SampleEntity> findByName(String name);
}

