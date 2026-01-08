package in.gov.manipur.rccms.controller;

import in.gov.manipur.rccms.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health Check Controller
 * Provides health check endpoint for monitoring application status
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Health", description = "Health check and system status endpoints")
public class HealthController {

    /**
     * Health check endpoint
     * GET /api/health
     * 
     * @return ApiResponse with health status message
     */
    @Operation(
            summary = "Health Check",
            description = "Returns the health status of the RCCMS Backend application. Use this endpoint to verify that the service is running and accessible."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Service is healthy and running",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(
                ApiResponse.success("RCCMS Backend Running")
        );
    }
}

