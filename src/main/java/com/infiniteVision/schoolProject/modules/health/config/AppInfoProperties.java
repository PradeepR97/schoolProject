package com.infiniteVision.schoolProject.modules.health.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Application metadata exposed by the health module.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "app")
public class AppInfoProperties {

    private String name = "School Management System";
}
