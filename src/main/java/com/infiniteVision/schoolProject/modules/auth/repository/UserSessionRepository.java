package com.infiniteVision.schoolProject.modules.auth.repository;

import com.infiniteVision.schoolProject.modules.auth.entity.UserSession;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Persistence for {@link UserSession} login/logout lifecycle.
 */
public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {

    Optional<UserSession> findBySessionId(UUID sessionId);

    /**
     * Ends all active sessions for a user (used on new login — single-session policy).
     */
    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE UserSession s
            SET s.sessionEnds = :ends
            WHERE s.userId = :userId AND s.sessionEnds IS NULL
            """)
    int endAllActiveSessionsForUser(@Param("userId") UUID userId, @Param("ends") LocalDateTime ends);

    /**
     * Ends one session by id (used on logout).
     */
    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE UserSession s
            SET s.sessionEnds = :ends
            WHERE s.sessionId = :sessionId AND s.sessionEnds IS NULL
            """)
    int endSession(@Param("sessionId") UUID sessionId, @Param("ends") LocalDateTime ends);
}
