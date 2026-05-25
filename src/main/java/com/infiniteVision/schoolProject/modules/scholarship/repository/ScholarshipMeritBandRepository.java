package com.infiniteVision.schoolProject.modules.scholarship.repository;

import com.infiniteVision.schoolProject.modules.scholarship.entity.ScholarshipMeritBand;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Merit mark-band configuration per academic year.
 */
@Repository
public interface ScholarshipMeritBandRepository extends JpaRepository<ScholarshipMeritBand, Long> {

    @Query(
            """
            SELECT b FROM ScholarshipMeritBand b
            JOIN FETCH b.academicYear
            WHERE b.deleted = false AND b.active = true
            AND b.academicYear.id = :academicYearId
            ORDER BY b.minMark DESC
            """)
    List<ScholarshipMeritBand> findActiveByAcademicYearIdOrderByMinMarkDesc(
            @Param("academicYearId") Long academicYearId);

    @Query(
            """
            SELECT b FROM ScholarshipMeritBand b
            WHERE b.deleted = false AND b.active = true
            AND b.academicYear.id = :academicYearId
            AND b.minMark <= :marks
            AND (b.maxMark IS NULL OR b.maxMark >= :marks)
            ORDER BY b.minMark DESC
            """)
    Optional<ScholarshipMeritBand> findMatchingBand(
            @Param("academicYearId") Long academicYearId, @Param("marks") BigDecimal marks);
}
