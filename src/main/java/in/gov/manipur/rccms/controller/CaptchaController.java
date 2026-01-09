package in.gov.manipur.rccms.controller;

import in.gov.manipur.rccms.dto.ApiResponse;
import in.gov.manipur.rccms.dto.CaptchaDTO;
import in.gov.manipur.rccms.service.CaptchaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * CAPTCHA Controller
 * Handles CAPTCHA generation and validation
 */
@Slf4j
@RestController
@RequestMapping("/api/auth/captcha")
@RequiredArgsConstructor
@Tag(name = "CAPTCHA", description = "CAPTCHA generation and validation endpoints")
public class CaptchaController {

    private final CaptchaService captchaService;

    /**
     * Generate CAPTCHA
     * GET /api/auth/captcha/generate
     */
    @Operation(
            summary = "Generate CAPTCHA",
            description = "Generate a new CAPTCHA code. Returns CAPTCHA ID and text."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "CAPTCHA generated successfully",
                    content = @Content(schema = @Schema(implementation = CaptchaDTO.class))
            )
    })
    @GetMapping("/generate")
    public ResponseEntity<ApiResponse<CaptchaDTO>> generateCaptcha(HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);
        log.debug("CAPTCHA generation request from IP: {}", ipAddress);
        
        CaptchaDTO captcha = captchaService.generateCaptcha(ipAddress);
        
        return ResponseEntity.ok(ApiResponse.success("CAPTCHA generated successfully", captcha));
    }

    /**
     * Validate CAPTCHA (Internal endpoint - can be used for testing)
     * POST /api/auth/captcha/validate
     */
    @Operation(
            summary = "Validate CAPTCHA",
            description = "Validate a CAPTCHA code. Returns validation result."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "CAPTCHA validation result",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateCaptcha(
            @RequestBody Map<String, String> request) {
        String captchaId = request.get("captchaId");
        String captchaText = request.get("captchaText");
        
        if (captchaId == null || captchaText == null) {
            throw new IllegalArgumentException("CAPTCHA ID and text are required");
        }
        
        boolean isValid = captchaService.validateCaptcha(captchaId, captchaText);
        
        Map<String, Object> response = Map.of(
                "valid", isValid,
                "message", isValid ? "Valid CAPTCHA" : "Invalid or expired CAPTCHA"
        );
        
        return ResponseEntity.ok(ApiResponse.success("CAPTCHA validation completed", response));
    }

    /**
     * Get client IP address
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }
}

