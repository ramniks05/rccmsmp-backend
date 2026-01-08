package in.gov.manipur.rccms.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI (Swagger) Configuration
 * Configures API documentation for the RCCMS Manipur Backend
 * 
 * Access Swagger UI at: http://localhost:8080/swagger-ui.html
 * Access API Docs JSON at: http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI rccmsOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Development Server");

        Server prodServer = new Server();
        prodServer.setUrl("https://api.rccms.manipur.gov.in");
        prodServer.setDescription("Production Server");

        Contact contact = new Contact();
        contact.setEmail("support@rccms.manipur.gov.in");
        contact.setName("RCCMS Support Team");
        contact.setUrl("https://rccms.manipur.gov.in");

        License license = new License()
                .name("Government of Manipur")
                .url("https://manipur.gov.in");

        Info info = new Info()
                .title("RCCMS Manipur Backend API")
                .version("1.0.0")
                .contact(contact)
                .description("""
                        RESTful API for Revenue Court Management System (RCCMS) Manipur.
                        
                        **Features:**
                        - Citizen registration and management
                        - JWT-based authentication
                        - Health check endpoints
                        - CORS enabled for Angular frontend
                        """)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer, prodServer));
    }
}

