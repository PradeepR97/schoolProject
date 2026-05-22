package com.infiniteVision.schoolProject.modules.student.repository;

import com.infiniteVision.schoolProject.modules.student.entity.StudentParent;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Persistence access for {@link StudentParent} records.
 */
@Repository
public interface StudentParentRepository extends JpaRepository<StudentParent, Long> {

    Optional<StudentParent> findByStudent_IdAndDeletedFalse(Long studentId);

    boolean existsByStudent_IdAndDeletedFalse(Long studentId);
}
