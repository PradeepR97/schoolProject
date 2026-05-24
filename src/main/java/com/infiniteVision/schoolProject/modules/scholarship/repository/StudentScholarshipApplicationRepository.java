package com.infiniteVision.schoolProject.modules.scholarship.repository;

import com.infiniteVision.schoolProject.modules.scholarship.entity.StudentScholarshipApplication;
import com.infiniteVision.schoolProject.modules.scholarship.enums.ScholarshipApplicationStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Persistence for {@link StudentScholarshipApplication} discount approval requests.
 */
@Repository
public interface StudentScholarshipApplicationRepository
        extends JpaRepository<StudentScholarshipApplication, Long> {

    Optional<StudentScholarshipApplication> findByIdAndDeletedFalse(Long id);

    boolean existsByStudent_IdAndScheme_IdAndAcademicYear_IdAndDeletedFalse(
            Long studentId, Long schemeId, Long academicYearId);

    @Query(
            """
            SELECT a FROM StudentScholarshipApplication a
            JOIN FETCH a.scheme s
            LEFT JOIN FETCH s.feeHead
            JOIN FETCH a.student
            JOIN FETCH a.academicYear
            WHERE a.id = :id AND a.deleted = false
            """)
    Optional<StudentScholarshipApplication> findActiveWithRelationsById(@Param("id") Long id);

    @Query(
            """
            SELECT a FROM StudentScholarshipApplication a
            JOIN FETCH a.scheme s
            LEFT JOIN FETCH s.feeHead
            WHERE a.student.id = :studentId
            AND a.academicYear.id = :academicYearId
            AND a.status = :status
            AND a.deleted = false
            """)
    List<StudentScholarshipApplication> findApprovedByStudentAndYear(
            @Param("studentId") Long studentId,
            @Param("academicYearId") Long academicYearId,
            @Param("status") ScholarshipApplicationStatus status);

    @Query(
            """
            SELECT a FROM StudentScholarshipApplication a
            JOIN FETCH a.scheme s
            LEFT JOIN FETCH s.feeHead
            WHERE a.student.id = :studentId
            AND a.academicYear.id = :academicYearId
            AND a.status IN :statuses
            AND a.deleted = false
            """)
    List<StudentScholarshipApplication> findByStudentAndYearAndStatusIn(
            @Param("studentId") Long studentId,
            @Param("academicYearId") Long academicYearId,
            @Param("statuses") List<ScholarshipApplicationStatus> statuses);

    @Query(
            value = """
                    SELECT a FROM StudentScholarshipApplication a
                    JOIN FETCH a.scheme s
                    LEFT JOIN FETCH s.feeHead
                    JOIN FETCH a.student
                    JOIN FETCH a.academicYear ay
                    WHERE a.deleted = false
                    AND (:status IS NULL OR a.status = :status)
                    AND (:studentId IS NULL OR a.student.id = :studentId)
                    AND (:academicYearId IS NULL OR ay.id = :academicYearId)
                    """,
            countQuery = """
                    SELECT COUNT(a) FROM StudentScholarshipApplication a
                    WHERE a.deleted = false
                    AND (:status IS NULL OR a.status = :status)
                    AND (:studentId IS NULL OR a.student.id = :studentId)
                    AND (:academicYearId IS NULL OR a.academicYear.id = :academicYearId)
                    """)
    org.springframework.data.domain.Page<StudentScholarshipApplication> findAllFiltered(
            @Param("status") ScholarshipApplicationStatus status,
            @Param("studentId") Long studentId,
            @Param("academicYearId") Long academicYearId,
            org.springframework.data.domain.Pageable pageable);

    @Query(
            """
            SELECT a FROM StudentScholarshipApplication a
            JOIN FETCH a.scheme s
            LEFT JOIN FETCH s.feeHead
            JOIN FETCH a.student
            JOIN FETCH a.academicYear
            WHERE a.status = :status AND a.deleted = false
            ORDER BY a.appliedAt DESC
            """)
    List<StudentScholarshipApplication> findAllByStatusAndDeletedFalseOrderByAppliedAtDesc(
            @Param("status") ScholarshipApplicationStatus status);

    @Query(
            """
            SELECT a FROM StudentScholarshipApplication a
            JOIN FETCH a.scheme s
            LEFT JOIN FETCH s.feeHead
            JOIN FETCH a.student
            JOIN FETCH a.academicYear
            WHERE a.student.id = :studentId AND a.deleted = false
            ORDER BY a.appliedAt DESC
            """)
    List<StudentScholarshipApplication> findAllByStudent_IdAndDeletedFalseOrderByAppliedAtDesc(
            @Param("studentId") Long studentId);

    @Query(
            """
            SELECT a FROM StudentScholarshipApplication a
            JOIN FETCH a.scheme s
            LEFT JOIN FETCH s.feeHead
            JOIN FETCH a.student
            JOIN FETCH a.academicYear
            WHERE a.deleted = false
            ORDER BY a.appliedAt DESC
            """)
    List<StudentScholarshipApplication> findAllActiveWithSchemeOrderByAppliedAtDesc();
}
