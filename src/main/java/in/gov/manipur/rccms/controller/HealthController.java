package in.gov.manipur.rccms.controller;

import in.gov.manipur.rccms.dto.ApiResponse;
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
public class HealthController {

    /**
     * Health check endpoint
     * GET /api/health
     * 
     * @return ApiResponse with health status message
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(
                ApiResponse.success("RCCMS Backend Running")
        );
    }
}

