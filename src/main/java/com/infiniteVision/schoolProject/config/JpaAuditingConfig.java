package com.infiniteVision.schoolProject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA/Hibernate setup for the authentication module.
 * <p>
 * Schema (tables, indexes, constraints) is generated and updated by Hibernate via
 * {@code spring.jpa.hibernate.ddl-auto} — no manual SQL migration scripts required.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "securityAuditorAware")
public class JpaAuditingConfig {
}
