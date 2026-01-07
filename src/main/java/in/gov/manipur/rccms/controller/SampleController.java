package in.gov.manipur.rccms.controller;

import in.gov.manipur.rccms.dto.ApiResponse;
import in.gov.manipur.rccms.entity.SampleEntity;
import in.gov.manipur.rccms.repository.SampleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Sample Controller for testing JPA functionality
 * Demonstrates basic CRUD operations
 */
@RestController
@RequestMapping("/api/samples")
@RequiredArgsConstructor
public class SampleController {

    private final SampleRepository sampleRepository;

    /**
     * Get all sample entities
     * GET /api/samples
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<SampleEntity>>> getAllSamples() {
        List<SampleEntity> samples = sampleRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success("Samples retrieved successfully", samples));
    }

    /**
     * Get sample entity by ID
     * GET /api/samples/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SampleEntity>> getSampleById(@PathVariable Long id) {
        return sampleRepository.findById(id)
                .map(sample -> ResponseEntity.ok(
                        ApiResponse.success("Sample retrieved successfully", sample)))
                .orElse(ResponseEntity.ok(
                        ApiResponse.error("Sample not found with id: " + id)));
    }

    /**
     * Create a new sample entity
     * POST /api/samples
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SampleEntity>> createSample(@RequestBody SampleEntity sample) {
        SampleEntity savedSample = sampleRepository.save(sample);
        return ResponseEntity.ok(
                ApiResponse.success("Sample created successfully", savedSample));
    }

    /**
     * Update an existing sample entity
     * PUT /api/samples/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SampleEntity>> updateSample(
            @PathVariable Long id,
            @RequestBody SampleEntity sample) {
        return sampleRepository.findById(id)
                .map(existingSample -> {
                    existingSample.setName(sample.getName());
                    existingSample.setDescription(sample.getDescription());
                    existingSample.setIsActive(sample.getIsActive());
                    SampleEntity updatedSample = sampleRepository.save(existingSample);
                    return ResponseEntity.ok(
                            ApiResponse.success("Sample updated successfully", updatedSample));
                })
                .orElse(ResponseEntity.ok(
                        ApiResponse.error("Sample not found with id: " + id)));
    }

    /**
     * Delete a sample entity
     * DELETE /api/samples/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSample(@PathVariable Long id) {
        if (sampleRepository.existsById(id)) {
            sampleRepository.deleteById(id);
            return ResponseEntity.ok(
                    ApiResponse.success("Sample deleted successfully", null));
        } else {
            return ResponseEntity.ok(
                    ApiResponse.error("Sample not found with id: " + id));
        }
    }
}

