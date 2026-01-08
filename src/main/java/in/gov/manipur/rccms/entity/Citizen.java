package in.gov.manipur.rccms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Citizen Entity
 * Represents a registered citizen in the RCCMS system
 */
@Entity
@Table(name = "citizens", 
       uniqueConstraints = @UniqueConstraint(columnNames = "mobile_number"))
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Citizen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @NotBlank(message = "Full name is mandatory")
    @Size(max = 200, message = "Full name must not exceed 200 characters")
    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @NotBlank(message = "Mobile number is mandatory")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Mobile number must be a valid 10-digit Indian mobile number")
    @Column(name = "mobile_number", nullable = false, unique = true, length = 10)
    private String mobileNumber;

    @Email(message = "Email must be a valid email address")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Column(name = "email_id", length = 100)
    private String emailId;

    @NotBlank(message = "District code is mandatory")
    @Size(max = 10, message = "District code must not exceed 10 characters")
    @Column(name = "district_code", nullable = false, length = 10)
    private String districtCode;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "aadhaar_number", length = 500) // Length 500 to store encrypted value
    private String aadhaarNumber; // Optional, will be encrypted at rest if provided

    @Column(name = "password", nullable = false, length = 255) // Length 255 for BCrypt hash
    private String password; // Will be hashed with BCrypt (validation done at DTO level)

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "registration_date", nullable = false, updatable = false)
    private LocalDate registrationDate;

    /**
     * Set registration date before persisting (if not already set)
     */
    @PrePersist
    protected void onCreate() {
        if (registrationDate == null) {
            registrationDate = LocalDate.now();
        }
        if (isActive == null) {
            isActive = true;
        }
    }
}

