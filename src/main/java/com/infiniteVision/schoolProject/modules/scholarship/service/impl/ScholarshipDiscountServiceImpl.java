package com.infiniteVision.schoolProject.modules.scholarship.service.impl;

import com.infiniteVision.schoolProject.modules.fees.entity.FeeStructure;
import com.infiniteVision.schoolProject.modules.payment.entity.StudentFeeLedger;
import com.infiniteVision.schoolProject.modules.payment.enums.LedgerStatus;
import com.infiniteVision.schoolProject.modules.payment.repository.InvoiceRepository;
import com.infiniteVision.schoolProject.modules.payment.repository.StudentFeeLedgerRepository;
import com.infiniteVision.schoolProject.modules.payment.util.PaymentInvoiceSyncHelper;
import com.infiniteVision.schoolProject.modules.payment.util.PaymentStatusCalculator;
import com.infiniteVision.schoolProject.modules.scholarship.entity.SchoolScheme;
import com.infiniteVision.schoolProject.modules.scholarship.entity.StudentScholarshipApplication;
import com.infiniteVision.schoolProject.modules.scholarship.enums.ScholarshipApplicationStatus;
import com.infiniteVision.schoolProject.modules.scholarship.repository.StudentScholarshipApplicationRepository;
import com.infiniteVision.schoolProject.modules.scholarship.service.ScholarshipDiscountService;
import com.infiniteVision.schoolProject.modules.scholarship.util.ScholarshipDiscountCalculator;
import com.infiniteVision.schoolProject.modules.scholarship.enums.ScholarshipApplicationStatus;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Applies approved scholarship discounts to fee ledgers and linked invoices (unpaid rows only).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScholarshipDiscountServiceImpl implements ScholarshipDiscountService {

    private static final List<ScholarshipApplicationStatus> PENDING_STATUSES =
            List.of(ScholarshipApplicationStatus.PENDING);

    private final StudentScholarshipApplicationRepository applicationRepository;
    private final StudentFeeLedgerRepository studentFeeLedgerRepository;
    private final PaymentInvoiceSyncHelper paymentInvoiceSyncHelper;

    @Override
    @Transactional(readOnly = true)
    public BigDecimal resolveApprovedDiscount(Long studentId, Long academicYearId, FeeStructure structure) {
        List<StudentScholarshipApplication> approved =
                findApprovedApplications(studentId, academicYearId);
        return sumDiscountFromApplications(approved, structure);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal resolvePendingDiscount(Long studentId, Long academicYearId, FeeStructure structure) {
        List<StudentScholarshipApplication> pending = applicationRepository.findByStudentAndYearAndStatusIn(
                studentId, academicYearId, PENDING_STATUSES);
        return sumDiscountFromApplications(pending, structure);
    }

    @Override
    @Transactional(readOnly = true)
    public ScholarshipApplicationStatus resolvePendingScholarshipStatus(
            Long studentId, Long academicYearId, FeeStructure structure) {
        List<StudentScholarshipApplication> pending = applicationRepository.findByStudentAndYearAndStatusIn(
                studentId, academicYearId, PENDING_STATUSES);
        for (StudentScholarshipApplication application : pending) {
            if (!ScholarshipDiscountCalculator.schemeAppliesToFeeStructure(application.getScheme(), structure)) {
                continue;
            }
            if (ScholarshipApplicationStatus.PENDING.equals(application.getStatus())) {
                return ScholarshipApplicationStatus.PENDING;
            }
        }
        return null;
    }

    private BigDecimal sumDiscountFromApplications(
            List<StudentScholarshipApplication> applications, FeeStructure structure) {
        BigDecimal actual = structure.getAmount() != null ? structure.getAmount() : BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        for (StudentScholarshipApplication application : applications) {
            SchoolScheme scheme = application.getScheme();
            if (!ScholarshipDiscountCalculator.schemeAppliesToFeeStructure(scheme, structure)) {
                continue;
            }
            java.math.BigDecimal effectivePercent = application.getApprovedDiscountPercent() != null
                    ? application.getApprovedDiscountPercent()
                    : application.getRequestedDiscountPercent();
            if (effectivePercent != null && effectivePercent.compareTo(BigDecimal.ZERO) > 0) {
                totalDiscount = totalDiscount.add(
                        ScholarshipDiscountCalculator.calculateDiscountFromPercent(actual, effectivePercent));
            } else {
                totalDiscount = totalDiscount.add(ScholarshipDiscountCalculator.calculateDiscount(scheme, actual));
            }
        }
        if (totalDiscount.compareTo(actual) > 0) {
            return actual;
        }
        return totalDiscount;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentScholarshipApplication> findApprovedApplications(Long studentId, Long academicYearId) {
        return applicationRepository.findApprovedByStudentAndYear(
                studentId, academicYearId, ScholarshipApplicationStatus.APPROVED);
    }

    @Override
    public void applyDiscountToLedger(StudentFeeLedger ledger, FeeStructure structure) {
        BigDecimal actual = structure.getAmount();
        BigDecimal discount = resolveApprovedDiscount(
                ledger.getStudent().getId(), ledger.getAcademicYear().getId(), structure);
        BigDecimal net = actual.subtract(discount).add(ledger.getLateFee() != null ? ledger.getLateFee() : BigDecimal.ZERO);
        BigDecimal paid = ledger.getPaidAmount() != null ? ledger.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal balance = net.subtract(paid);
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            balance = BigDecimal.ZERO;
        }
        ledger.setActualAmount(actual);
        ledger.setDiscountAmount(discount);
        ledger.setNetAmount(net);
        ledger.setBalanceAmount(balance);
        ledger.setStatus(PaymentStatusCalculator.resolveLedgerStatus(net, paid, balance, ledger.getDueDate()));
    }

    @Override
    @Transactional
    public void recalculateUnpaidLedgersForStudent(Long studentId, Long academicYearId) {
        List<StudentFeeLedger> ledgers =
                studentFeeLedgerRepository.findAllActiveWithFeeTypeByStudentIdAndYear(studentId, academicYearId);
        for (StudentFeeLedger ledger : ledgers) {
            if (LedgerStatus.PAID.equals(ledger.getStatus())
                    || ledger.getBalanceAmount().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            FeeStructure structure = ledger.getFeeStructure();
            if (structure == null) {
                continue;
            }
            applyDiscountToLedger(ledger, structure);
            studentFeeLedgerRepository.save(ledger);
            paymentInvoiceSyncHelper.syncInvoiceFromLedger(ledger);
            log.info(
                    "Recalculated ledger id={} studentId={} discount={} net={}",
                    ledger.getId(),
                    studentId,
                    ledger.getDiscountAmount(),
                    ledger.getNetAmount());
        }
    }
}
