package in.gov.manipur.rccms.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "case_category")
public class CaseCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_type_id", nullable = false, foreignKey = @ForeignKey(name = "fk_form_field_case_type"))
    private CaseType caseType;

    @Column(name = "case_type_id", insertable = false, updatable = false)
    private Long caseTypeId;

    @Column(name = "category_name", nullable = false, length = 100)
    private String categoryName;
    @Column(name = "priority", nullable = false)
    private Integer displayOrder;
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
