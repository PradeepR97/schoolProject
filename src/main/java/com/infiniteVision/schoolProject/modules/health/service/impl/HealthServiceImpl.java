package com.infiniteVision.schoolProject.modules.health.service.impl;

import com.infiniteVision.schoolProject.modules.health.config.AppInfoProperties;
import com.infiniteVision.schoolProject.modules.health.constants.HealthConstants;
import com.infiniteVision.schoolProject.modules.health.dto.HealthCheckOutcome;
import com.infiniteVision.schoolProject.modules.health.dto.response.HealthCheckDataDTO;
import com.infiniteVision.schoolProject.modules.health.service.HealthService;
import java.time.LocalDateTime;
import java.util.List;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Service;

/**
 * Verifies MySQL and Redis connectivity for the health endpoint.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HealthServiceImpl implements HealthService {

    private final DataSource dataSource;
    private final RedisConnectionFactory redisConnectionFactory;
    private final AppInfoProperties appInfoProperties;

    /**
     * Run dependency checks and build a success or failure outcome for the controller.
     */
    @Override
    public HealthCheckOutcome checkHealth() {
        LocalDateTime serverTime = LocalDateTime.now();
        boolean databaseHealthy = isDatabaseHealthy();
        boolean redisHealthy = isRedisHealthy();

        if (!databaseHealthy) {
            log.warn("Health check failed: database unavailable");
            return HealthCheckOutcome.builder()
                    .healthy(false)
                    .message(HealthConstants.DATABASE_CONNECTION_FAILED)
                    .errors(List.of(HealthConstants.MYSQL_SERVICE_UNAVAILABLE))
                    .build();
        }

        if (!redisHealthy) {
            log.warn("Health check failed: redis unavailable");
            return HealthCheckOutcome.builder()
                    .healthy(false)
                    .message(HealthConstants.REDIS_CONNECTION_FAILED)
                    .errors(List.of(HealthConstants.REDIS_SERVICE_UNAVAILABLE))
                    .build();
        }

        log.debug("Health check passed");
        HealthCheckDataDTO data = HealthCheckDataDTO.builder()
                .application(appInfoProperties.getName())
                .status(HealthConstants.STATUS_UP)
                .serverTime(serverTime)
                .database(HealthConstants.CONNECTED)
                .redis(HealthConstants.CONNECTED)
                .build();

        return HealthCheckOutcome.builder()
                .healthy(true)
                .message(HealthConstants.HEALTH_CHECK_SUCCESS)
                .data(data)
                .build();
    }

    /**
     * Validate MySQL using the configured {@link DataSource}.
     */
    private boolean isDatabaseHealthy() {
        try (var connection = dataSource.getConnection()) {
            return connection.isValid(2);
        } catch (Exception exception) {
            log.error("Database health check failed", exception);
            return false;
        }
    }

    /**
     * Validate Redis using a ping against the connection factory.
     */
    private boolean isRedisHealthy() {
        try (var connection = redisConnectionFactory.getConnection()) {
            return "PONG".equalsIgnoreCase(connection.ping());
        } catch (Exception exception) {
            log.error("Redis health check failed", exception);
            return false;
        }
    }
}
