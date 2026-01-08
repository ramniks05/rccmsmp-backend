package in.gov.manipur.rccms.controller;

import in.gov.manipur.rccms.dto.ApiResponse;
import in.gov.manipur.rccms.dto.LoginRequest;
import in.gov.manipur.rccms.dto.LoginResponse;
import in.gov.manipur.rccms.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 * Handles citizen login and authentication
 * Angular compatible with CORS enabled
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Citizen authentication and login endpoints")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    /**
     * Login endpoint
     * POST /api/auth/login
     * Username can be mobile number or email ID
     */
    @Operation(
            summary = "Citizen Login",
            description = "Authenticate citizen with mobile number or email ID and password. Returns JWT token for subsequent API calls."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request received for username: {}", maskUsername(request.getUsername()));
        
        LoginResponse response = authenticationService.login(request);
        
        return ResponseEntity.ok(
                ApiResponse.success("Login successful", response));
    }

    /**
     * Mask username for logging (privacy)
     */
    private String maskUsername(String username) {
        if (username == null || username.length() <= 4) {
            return "****";
        }
        return username.substring(0, 2) + "****" + username.substring(username.length() - 2);
    }
}

