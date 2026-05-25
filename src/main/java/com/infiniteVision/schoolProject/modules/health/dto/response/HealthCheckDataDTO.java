package com.infiniteVision.schoolProject.modules.health.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Health check payload returned inside {@code data} on success.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthCheckDataDTO {

    private String application;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime serverTime;
    private String database;
    private String redis;
}
