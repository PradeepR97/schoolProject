package com.infiniteVision.schoolProject.modules.health.service;

import com.infiniteVision.schoolProject.modules.health.dto.HealthCheckOutcome;

/**
 * Application and dependency health checks.
 */
public interface HealthService {

    HealthCheckOutcome checkHealth();
}
