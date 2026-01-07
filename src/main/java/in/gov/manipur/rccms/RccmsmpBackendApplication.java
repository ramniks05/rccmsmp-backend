package in.gov.manipur.rccms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main Spring Boot Application Class
 * RCCMS Manipur Backend Application
 * 
 * @EnableJpaAuditing enables automatic auditing for JPA entities
 * (createdAt, updatedAt fields)
 */
@SpringBootApplication
@EnableJpaAuditing
public class RccmsmpBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(RccmsmpBackendApplication.class, args);
    }
}

