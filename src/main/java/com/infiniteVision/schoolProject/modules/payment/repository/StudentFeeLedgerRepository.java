package com.infiniteVision.schoolProject.modules.payment.repository;

import com.infiniteVision.schoolProject.modules.payment.entity.StudentFeeLedger;
import com.infiniteVision.schoolProject.modules.payment.enums.FeeBillingTerm;
import com.infiniteVision.schoolProject.modules.payment.enums.LedgerStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Persistence access for {@link StudentFeeLedger} records.
 */
@Repository
public interface StudentFeeLedgerRepository extends JpaRepository<StudentFeeLedger, Long> {

    Optional<StudentFeeLedger> findByIdAndDeletedFalse(Long id);

    List<StudentFeeLedger> findAllByStudent_IdAndDeletedFalseOrderByDueDateAsc(Long studentId);

    List<StudentFeeLedger> findAllByStudent_IdAndAcademicYear_IdAndDeletedFalseOrderByDueDateAsc(
            Long studentId, Long academicYearId);

    @Query(
            """
            SELECT l FROM StudentFeeLedger l
            JOIN FETCH l.feeStructure fs
            JOIN FETCH fs.feeHead
            WHERE l.student.id = :studentId AND l.deleted = false
            ORDER BY l.dueDate ASC
            """)
    List<StudentFeeLedger> findAllActiveWithFeeHeadByStudentId(@Param("studentId") Long studentId);

    @Query(
            """
            SELECT l FROM StudentFeeLedger l
            JOIN FETCH l.feeStructure fs
            JOIN FETCH fs.feeHead
            WHERE l.student.id = :studentId
            AND l.academicYear.id = :academicYearId
            AND l.deleted = false
            ORDER BY l.dueDate ASC
            """)
    List<StudentFeeLedger> findAllActiveWithFeeHeadByStudentIdAndYear(
            @Param("studentId") Long studentId, @Param("academicYearId") Long academicYearId);

    boolean existsByStudent_IdAndAcademicYear_IdAndTermAndFeeStructure_IdAndDeletedFalse(
            Long studentId, Long academicYearId, FeeBillingTerm term, Long structureId);

    @Query(
            """
            SELECT l FROM StudentFeeLedger l
            JOIN FETCH l.student
            JOIN FETCH l.feeStructure fs
            JOIN FETCH fs.feeHead
            JOIN FETCH l.academicYear
            WHERE l.id = :id AND l.deleted = false
            """)
    Optional<StudentFeeLedger> findActiveWithRelationsById(@Param("id") Long id);

    @Query(
            """
            SELECT l FROM StudentFeeLedger l
            JOIN FETCH l.student s
            JOIN FETCH l.feeStructure fs
            JOIN FETCH fs.feeHead
            JOIN FETCH l.academicYear
            WHERE l.deleted = false AND s.deleted = false
            AND l.balanceAmount > :zero
            AND (:academicYearId IS NULL OR l.academicYear.id = :academicYearId)
            AND (:classId IS NULL OR s.classId = :classId)
            AND (:overdueOnly = false OR l.dueDate < CURRENT_DATE OR l.status = :overdueStatus)
            ORDER BY s.admissionNo ASC, l.dueDate ASC
            """)
    List<StudentFeeLedger> findAllOutstandingLedgers(
            @Param("academicYearId") Long academicYearId,
            @Param("classId") Long classId,
            @Param("overdueOnly") boolean overdueOnly,
            @Param("overdueStatus") LedgerStatus overdueStatus,
            @Param("zero") BigDecimal zero);

    @Query(
            """
            SELECT COALESCE(SUM(l.balanceAmount), 0) FROM StudentFeeLedger l
            JOIN l.student s
            WHERE l.deleted = false AND s.deleted = false AND l.balanceAmount > :zero
            AND (:academicYearId IS NULL OR l.academicYear.id = :academicYearId)
            AND (:classId IS NULL OR s.classId = :classId)
            AND (:overdueOnly = false OR l.dueDate < CURRENT_DATE OR l.status = :overdueStatus)
            """)
    BigDecimal sumOutstandingBalance(
            @Param("academicYearId") Long academicYearId,
            @Param("classId") Long classId,
            @Param("overdueOnly") boolean overdueOnly,
            @Param("overdueStatus") LedgerStatus overdueStatus,
            @Param("zero") BigDecimal zero);
}
