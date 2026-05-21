package com.infiniteVision.schoolProject.modules.auth.entity;

import com.infiniteVision.schoolProject.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Persistent login session for audit and history.
 * <p>
 * Maps to {@code user_sessions}. Inherits audit and soft-delete fields from {@link BaseEntity}.
 * Active sessions have {@code session_ends = null}. Bearer token references {@code session_id} (UUID).
 */
@Entity
@Table(
        name = "user_sessions",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_sessions_session_id", columnNames = "session_id")
        },
        indexes = {
                @Index(name = "idx_user_sessions_user_id", columnList = "user_id"),
                @Index(name = "idx_user_sessions_active", columnList = "user_id, session_ends"),
                @Index(name = "idx_user_sessions_deleted", columnList = "deleted")
        })
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserSession extends BaseEntity {

    @NotNull
    @Column(name = "session_id", nullable = false, updatable = false, length = 36)
    private UUID sessionId;

    @NotNull
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @NotNull
    @Column(name = "session_starts", nullable = false, updatable = false)
    private LocalDateTime sessionStarts;

    @Column(name = "session_ends")
    private LocalDateTime sessionEnds;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}
