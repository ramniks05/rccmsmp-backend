package in.gov.manipur.rccms.controller;

import in.gov.manipur.rccms.dto.ApiResponse;
import in.gov.manipur.rccms.dto.CitizenRegistrationRequest;
import in.gov.manipur.rccms.dto.CitizenResponse;
import in.gov.manipur.rccms.service.CitizenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Citizen Controller
 * Handles citizen registration and related operations
 * Angular compatible with CORS enabled
 */
@Slf4j
@RestController
@RequestMapping("/api/citizens")
@RequiredArgsConstructor
@Tag(name = "Citizen", description = "Citizen registration and management endpoints")
public class CitizenController {

    private final CitizenService citizenService;

    /**
     * Register a new citizen
     * POST /api/citizens/register
     */
    @Operation(
            summary = "Register a new citizen",
            description = "Registers a new citizen in the RCCMS system. Mobile number must be unique."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Citizen registered successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Validation error or duplicate mobile number",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<CitizenResponse>> registerCitizen(
            @Valid @RequestBody CitizenRegistrationRequest request) {
        log.info("Received registration request for mobile: {}", request.getMobileNumber());
        
        CitizenResponse response = citizenService.registerCitizen(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Citizen registered successfully", response));
    }

    /**
     * Get citizen by ID
     * GET /api/citizens/{id}
     */
    @Operation(
            summary = "Get citizen by ID",
            description = "Retrieves citizen information by their unique ID"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Citizen found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Citizen not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CitizenResponse>> getCitizenById(
            @Parameter(description = "Citizen ID", required = true)
            @PathVariable Long id) {
        log.debug("Fetching citizen with ID: {}", id);
        
        CitizenResponse response = citizenService.getCitizenById(id);
        
        return ResponseEntity.ok(
                ApiResponse.success("Citizen retrieved successfully", response));
    }

    /**
     * Get citizen by mobile number
     * GET /api/citizens/mobile/{mobileNumber}
     */
    @Operation(
            summary = "Get citizen by mobile number",
            description = "Retrieves citizen information by their mobile number"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Citizen found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Citizen not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @GetMapping("/mobile/{mobileNumber}")
    public ResponseEntity<ApiResponse<CitizenResponse>> getCitizenByMobileNumber(
            @Parameter(description = "Mobile number (10 digits)", required = true)
            @PathVariable String mobileNumber) {
        log.debug("Fetching citizen with mobile number: {}", mobileNumber);
        
        CitizenResponse response = citizenService.getCitizenByMobileNumber(mobileNumber);
        
        return ResponseEntity.ok(
                ApiResponse.success("Citizen retrieved successfully", response));
    }
}

