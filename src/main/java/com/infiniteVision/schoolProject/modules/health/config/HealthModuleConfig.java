package com.infiniteVision.schoolProject.modules.health.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Health module Spring configuration.
 */
@Configuration
@EnableConfigurationProperties(AppInfoProperties.class)
public class HealthModuleConfig {
}
