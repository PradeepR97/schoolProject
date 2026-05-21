package com.infiniteVision.schoolProject.modules.health.controller;

import com.infiniteVision.schoolProject.common.dto.response.ApiResponse;
import com.infiniteVision.schoolProject.common.dto.response.ErrorResponse;
import com.infiniteVision.schoolProject.modules.health.constants.HealthApiConstants;
import com.infiniteVision.schoolProject.modules.health.dto.HealthCheckOutcome;
import com.infiniteVision.schoolProject.modules.health.service.HealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public health endpoint (no authorization required).
 */
@RestController
@RequestMapping(value = HealthApiConstants.BASE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class HealthController {

    private final HealthService healthService;

    @GetMapping
    public ResponseEntity<?> checkHealth() {
        HealthCheckOutcome outcome = healthService.checkHealth();

        if (outcome.isHealthy()) {
            return ResponseEntity.ok(ApiResponse.success(outcome.getMessage(), outcome.getData()));
        }

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ErrorResponse.of(outcome.getMessage(), outcome.getErrors()));
    }
}
