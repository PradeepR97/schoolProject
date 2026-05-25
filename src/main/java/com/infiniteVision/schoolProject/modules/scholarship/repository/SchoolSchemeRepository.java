package com.infiniteVision.schoolProject.modules.scholarship.repository;

import com.infiniteVision.schoolProject.modules.scholarship.entity.SchoolScheme;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Persistence access for {@link SchoolScheme} records.
 */
@Repository
public interface SchoolSchemeRepository extends JpaRepository<SchoolScheme, Long> {

    List<SchoolScheme> findByIsActiveTrueAndDeletedFalseOrderBySchemeNameAsc();

    Optional<SchoolScheme> findByIdAndIsActiveTrueAndDeletedFalse(Long id);

    Optional<SchoolScheme> findByIdAndDeletedFalse(Long id);
}
