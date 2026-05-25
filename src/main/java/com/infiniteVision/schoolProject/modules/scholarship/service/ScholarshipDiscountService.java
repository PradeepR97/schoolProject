package com.infiniteVision.schoolProject.modules.scholarship.service;

import com.infiniteVision.schoolProject.modules.fees.entity.FeeStructure;
import com.infiniteVision.schoolProject.modules.payment.entity.StudentFeeLedger;
import com.infiniteVision.schoolProject.modules.scholarship.entity.StudentScholarshipApplication;
import com.infiniteVision.schoolProject.modules.scholarship.enums.ScholarshipApplicationStatus;
import java.math.BigDecimal;
import java.util.List;

/**
 * Resolves approved scholarship discounts and syncs fee ledgers.
 */
public interface ScholarshipDiscountService {

    /**
     * Sum of discounts from all {@link ScholarshipApplicationStatus#APPROVED} applications
     * that apply to the given fee structure (capped at actual amount).
     */
    BigDecimal resolveApprovedDiscount(Long studentId, Long academicYearId, FeeStructure structure);

    /**
     * Sum of discounts from in-flight {@link ScholarshipApplicationStatus#PENDING} applications.
     */
    BigDecimal resolvePendingDiscount(Long studentId, Long academicYearId, FeeStructure structure);

    /**
     * Highest-priority in-flight scholarship status for a fee structure, if any.
     */
    ScholarshipApplicationStatus resolvePendingScholarshipStatus(
            Long studentId, Long academicYearId, FeeStructure structure);

    /**
     * Recalculates unpaid ledgers for a student/year after scholarship approval.
     */
    void recalculateUnpaidLedgersForStudent(Long studentId, Long academicYearId);

    /**
     * Applies discount and net/balance to a new or existing unpaid ledger row.
     */
    void applyDiscountToLedger(StudentFeeLedger ledger, FeeStructure structure);

    List<StudentScholarshipApplication> findApprovedApplications(Long studentId, Long academicYearId);
}
