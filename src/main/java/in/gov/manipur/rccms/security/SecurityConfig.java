package in.gov.manipur.rccms.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Spring Security Configuration
 * 
 * Current configuration:
 * - Permits all requests (no authentication required)
 * - Disables CSRF protection
 * - Enables CORS
 * - Stateless session management
 * 
 * This is a basic setup ready for JWT implementation in the future
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(CorsConfigurationSource corsConfigurationSource) {
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for now (will be enabled with JWT)
                .csrf(csrf -> csrf.disable())
                
                // Enable CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                
                // Permit all requests (no authentication)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().permitAll()
                )
                
                // Stateless session management (for JWT in future)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                
                // Allow H2 console frames (for development)
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.disable())
                );

        return http.build();
    }
}

