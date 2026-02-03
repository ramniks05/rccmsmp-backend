package in.gov.manipur.rccms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Spring Boot Application Class
 * RCCMS Manipur Backend Application
 * 
 * @EnableJpaAuditing enables automatic auditing for JPA entities
 * @EnableScheduling enables scheduled tasks (OTP cleanup)
 * @EntityScan ensures entities are scanned
 * @EnableJpaRepositories ensures repositories are enabled
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@EntityScan(basePackages = "in.gov.manipur.rccms.entity")
@EnableJpaRepositories(basePackages = "in.gov.manipur.rccms.repository")
public class RccmsmpBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(RccmsmpBackendApplication.class, args);
    }
}

