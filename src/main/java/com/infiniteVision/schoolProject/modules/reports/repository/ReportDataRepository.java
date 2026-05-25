package com.infiniteVision.schoolProject.modules.reports.repository;

import com.infiniteVision.schoolProject.modules.payment.entity.Payment;
import com.infiniteVision.schoolProject.modules.payment.entity.StudentFeeLedger;
import com.infiniteVision.schoolProject.modules.payment.enums.PaymentRecordStatus;
import com.infiniteVision.schoolProject.modules.scholarship.entity.StudentScholarshipApplication;
import com.infiniteVision.schoolProject.modules.student.entity.Student;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Read-only queries for report exports.
 */
@Repository
public interface ReportDataRepository extends JpaRepository<Student, Long> {

    @Query(
            """
            SELECT s FROM Student s
            LEFT JOIN FETCH s.parents
            WHERE s.deleted = false
            AND s.academicYearId = :academicYearId
            AND (:classId IS NULL OR s.classId = :classId)
            ORDER BY s.classId ASC, s.admissionNo ASC
            """)
    List<Student> findStudentsForReport(
            @Param("academicYearId") Long academicYearId, @Param("classId") Long classId);

    @Query(
            """
            SELECT p FROM Payment p
            JOIN FETCH p.student s
            JOIN FETCH p.ledger
            JOIN FETCH p.invoice i
            WHERE p.deleted = false
            AND p.status = :status
            AND s.academicYearId = :academicYearId
            AND (:classId IS NULL OR s.classId = :classId)
            AND p.paymentDate >= :fromDate
            AND p.paymentDate <= :toDate
            ORDER BY p.paymentDate ASC, p.receiptNo ASC
            """)
    List<Payment> findPaymentsForFeeCollectionReport(
            @Param("academicYearId") Long academicYearId,
            @Param("classId") Long classId,
            @Param("status") PaymentRecordStatus status,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    @Query(
            """
            SELECT l FROM StudentFeeLedger l
            JOIN FETCH l.student s
            LEFT JOIN FETCH s.parents
            JOIN FETCH l.feeStructure fs
            JOIN FETCH fs.feeHead
            WHERE l.deleted = false
            AND l.academicYear.id = :academicYearId
            AND l.balanceAmount > 0
            AND (:classId IS NULL OR s.classId = :classId)
            AND l.dueDate >= :fromDueDate
            AND l.dueDate <= :toDueDate
            ORDER BY s.classId ASC, l.dueDate ASC
            """)
    List<StudentFeeLedger> findPendingFeesForReport(
            @Param("academicYearId") Long academicYearId,
            @Param("classId") Long classId,
            @Param("fromDueDate") LocalDate fromDueDate,
            @Param("toDueDate") LocalDate toDueDate);

    @Query(
            """
            SELECT a FROM StudentScholarshipApplication a
            JOIN FETCH a.scheme s
            LEFT JOIN FETCH s.feeHead
            JOIN FETCH a.student st
            JOIN FETCH a.academicYear
            WHERE a.deleted = false
            AND a.academicYear.id = :academicYearId
            AND (:classId IS NULL OR st.classId = :classId)
            AND a.appliedAt >= :fromAppliedAt
            AND a.appliedAt <= :toAppliedAt
            ORDER BY a.status ASC, a.appliedAt DESC
            """)
    List<StudentScholarshipApplication> findScholarshipApplicationsForReport(
            @Param("academicYearId") Long academicYearId,
            @Param("classId") Long classId,
            @Param("fromAppliedAt") LocalDateTime fromAppliedAt,
            @Param("toAppliedAt") LocalDateTime toAppliedAt);
}
