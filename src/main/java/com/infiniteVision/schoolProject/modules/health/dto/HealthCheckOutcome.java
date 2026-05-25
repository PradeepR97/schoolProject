package com.infiniteVision.schoolProject.modules.health.dto;

import com.infiniteVision.schoolProject.modules.health.dto.response.HealthCheckDataDTO;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * Internal result of a health check; mapped to HTTP 200 or 503 by the controller.
 */
@Getter
@Builder
public class HealthCheckOutcome {

    private final boolean healthy;
    private final String message;
    private final HealthCheckDataDTO data;
    private final List<String> errors;

    public List<String> getErrors() {
        return errors == null ? Collections.emptyList() : errors;
    }
}
