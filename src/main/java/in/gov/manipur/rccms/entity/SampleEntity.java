package in.gov.manipur.rccms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Sample Entity for testing JPA functionality
 * This demonstrates how to extend BaseEntity and use JPA annotations
 */
@Entity
@Table(name = "sample_entities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SampleEntity extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true;
}

