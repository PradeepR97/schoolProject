package com.infiniteVision.schoolProject.modules.auth.repository;

import com.infiniteVision.schoolProject.modules.auth.entity.User;
import com.infiniteVision.schoolProject.modules.auth.enums.UserRole;
import com.infiniteVision.schoolProject.modules.auth.enums.UserStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Persistence for {@link User} login lookups.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailAndDeletedFalse(String email);

    Optional<User> findByPhoneAndDeletedFalse(String phone);

    boolean existsByUsernameAndDeletedFalse(String username);

    boolean existsByEmailAndDeletedFalse(String email);

    boolean existsByPhoneAndDeletedFalse(String phone);

    /** Matches DB unique index (all rows, including soft-deleted). */
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    List<User> findAllByDeletedFalseOrderByIdAsc();

    Optional<User> findByIdAndDeletedFalse(Long id);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByPhoneAndIdNot(String phone, Long id);

    long countByRoleAndDeletedFalse(UserRole role);

    @Query(
            """
            SELECT u FROM User u
            WHERE u.deleted = false AND u.status = :activeStatus AND u.role IN :roles
            """)
    List<User> findAllByRoleInAndDeletedFalseAndStatusActive(
            @Param("roles") List<UserRole> roles, @Param("activeStatus") UserStatus activeStatus);
}
