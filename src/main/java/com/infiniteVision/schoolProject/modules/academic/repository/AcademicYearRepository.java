package com.infiniteVision.schoolProject.modules.academic.repository;

import com.infiniteVision.schoolProject.modules.academic.entity.AcademicYear;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Persistence access for {@link AcademicYear} records.
 */
@Repository
public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {

    Optional<AcademicYear> findByIdAndDeletedFalse(Long id);

    Optional<AcademicYear> findByYearNameAndDeletedFalse(String yearName);

    Optional<AcademicYear> findByCurrentTrueAndDeletedFalse();

    List<AcademicYear> findAllByDeletedFalseOrderByStartDateDesc();

    boolean existsByYearNameAndDeletedFalse(String yearName);

    boolean existsByYearNameAndIdNotAndDeletedFalse(String yearName, Long id);
}
