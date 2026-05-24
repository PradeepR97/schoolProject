package com.infiniteVision.schoolProject.common.audit.repository;

import com.infiniteVision.schoolProject.common.audit.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Persistence for immutable {@link AuditLog} rows.
 */
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
