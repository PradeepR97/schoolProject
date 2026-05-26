package com.infiniteVision.schoolProject.modules.dashboard.repository;

import com.infiniteVision.schoolProject.modules.dashboard.entity.DashboardActivity;
import com.infiniteVision.schoolProject.modules.dashboard.repository.projection.PendingScholarshipProjection;
import com.infiniteVision.schoolProject.modules.dashboard.repository.projection.RecentAdmissionProjection;
import com.infiniteVision.schoolProject.modules.dashboard.repository.projection.RecentPaymentProjection;
import com.infiniteVision.schoolProject.modules.payment.enums.LedgerStatus;
import com.infiniteVision.schoolProject.modules.payment.enums.PaymentRecordStatus;
import com.infiniteVision.schoolProject.modules.scholarship.enums.ScholarshipApplicationStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Read-only aggregate and projection queries for the dashboard summary API.
 */
@Repository
public interface DashboardSummaryRepository extends JpaRepository<DashboardActivity, Long> {

    @Query(
            """
            SELECT COUNT(s) FROM Student s
            WHERE s.deleted = false
            AND (:academicYearId IS NULL OR s.academicYearId = :academicYearId)
            """)
    long countActiveStudents(@Param("academicYearId") Long academicYearId);

    @Query(
            """
            SELECT COALESCE(SUM(p.amountPaid), 0) FROM Payment p
            WHERE p.deleted = false
            AND p.status = :successStatus
            AND (:academicYearId IS NULL OR p.student.academicYearId = :academicYearId)
            """)
    BigDecimal sumFeeCollected(
            @Param("academicYearId") Long academicYearId,
            @Param("successStatus") PaymentRecordStatus successStatus);

    @Query(
            """
            SELECT COALESCE(SUM(l.balanceAmount), 0) FROM StudentFeeLedger l
            WHERE l.deleted = false
            AND l.status IN :pendingStatuses
            AND (:academicYearId IS NULL OR l.academicYear.id = :academicYearId)
            """)
    BigDecimal sumPendingFees(
            @Param("academicYearId") Long academicYearId,
            @Param("pendingStatuses") List<LedgerStatus> pendingStatuses);

    @Query(
            """
            SELECT COUNT(s) FROM Student s
            WHERE s.deleted = false
            AND s.createdAt >= :fromDate
            AND (:academicYearId IS NULL OR s.academicYearId = :academicYearId)
            """)
    long countNewAdmissionsSince(
            @Param("academicYearId") Long academicYearId,
            @Param("fromDate") LocalDateTime fromDate);

    @Query(
            """
            SELECT p.receiptNo AS receiptNo,
                   CONCAT(s.firstName, ' ', COALESCE(s.lastName, '')) AS studentName,
                   CONCAT(c.className, ' - ', sec.sectionCode) AS className,
                   p.amountPaid AS amount,
                   p.paymentMode AS paymentMethod,
                   p.status AS paymentStatus
            FROM Payment p
            JOIN p.student s
            LEFT JOIN ClassMaster c ON c.id = s.classId AND c.deleted = false
            LEFT JOIN SectionMaster sec ON sec.id = s.sectionId AND sec.deleted = false
            WHERE p.deleted = false
            ORDER BY p.createdAt DESC
            """)
    List<RecentPaymentProjection> findRecentPayments(Pageable pageable);

    @Query(
            """
            SELECT s.admissionNo AS applicationNo,
                   CONCAT(s.firstName, ' ', COALESCE(s.lastName, '')) AS studentName,
                   CONCAT(c.className, ' - ', sec.sectionCode) AS className,
                   s.status AS status
            FROM Student s
            LEFT JOIN ClassMaster c ON c.id = s.classId AND c.deleted = false
            LEFT JOIN SectionMaster sec ON sec.id = s.sectionId AND sec.deleted = false
            WHERE s.deleted = false
            AND (:academicYearId IS NULL OR s.academicYearId = :academicYearId)
            ORDER BY s.createdAt DESC
            """)
    List<RecentAdmissionProjection> findRecentAdmissions(
            @Param("academicYearId") Long academicYearId, Pageable pageable);

    @Query(
            """
            SELECT CONCAT(st.firstName, ' ', COALESCE(st.lastName, '')) AS studentName,
                   sch.schemeName AS schemeName,
                   sch.discountValue AS amount,
                   a.status AS status
            FROM StudentScholarshipApplication a
            JOIN a.student st
            JOIN a.scheme sch
            WHERE a.deleted = false
            AND a.status IN :pendingStatuses
            AND (:academicYearId IS NULL OR a.academicYear.id = :academicYearId)
            ORDER BY a.appliedAt DESC
            """)
    List<PendingScholarshipProjection> findPendingScholarships(
            @Param("academicYearId") Long academicYearId,
            @Param("pendingStatuses") List<ScholarshipApplicationStatus> pendingStatuses,
            Pageable pageable);
}
