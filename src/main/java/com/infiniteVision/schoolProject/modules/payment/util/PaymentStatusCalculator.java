package com.infiniteVision.schoolProject.modules.payment.util;

import com.infiniteVision.schoolProject.modules.payment.enums.InvoiceStatus;
import com.infiniteVision.schoolProject.modules.payment.enums.LedgerStatus;
import com.infiniteVision.schoolProject.modules.student.enums.FeesPaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Derives ledger, invoice, and student fee statuses from amounts and due dates.
 */
public final class PaymentStatusCalculator {

    private PaymentStatusCalculator() {
    }

    public static LedgerStatus resolveLedgerStatus(
            BigDecimal netAmount, BigDecimal paidAmount, BigDecimal balanceAmount, LocalDate dueDate) {
        if (balanceAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return LedgerStatus.PAID;
        }
        if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
            if (dueDate != null && dueDate.isBefore(LocalDate.now())) {
                return LedgerStatus.OVERDUE;
            }
            return LedgerStatus.PARTIAL;
        }
        if (dueDate != null && dueDate.isBefore(LocalDate.now())) {
            return LedgerStatus.OVERDUE;
        }
        return LedgerStatus.PENDING;
    }

    public static InvoiceStatus resolveInvoiceStatus(
            BigDecimal balanceAmount, BigDecimal paidAmount, LocalDate dueDate, InvoiceStatus current) {
        if (balanceAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return InvoiceStatus.PAID;
        }
        if (paidAmount.compareTo(BigDecimal.ZERO) > 0) {
            if (dueDate != null && dueDate.isBefore(LocalDate.now())) {
                return InvoiceStatus.OVERDUE;
            }
            return InvoiceStatus.PARTIAL;
        }
        if (dueDate != null && dueDate.isBefore(LocalDate.now())) {
            return InvoiceStatus.OVERDUE;
        }
        if (current == InvoiceStatus.DRAFT || current == InvoiceStatus.SENT) {
            return current;
        }
        return InvoiceStatus.SENT;
    }

    public static FeesPaymentStatus resolveStudentFeesStatus(List<LedgerStatus> ledgerStatuses) {
        if (ledgerStatuses == null || ledgerStatuses.isEmpty()) {
            return FeesPaymentStatus.PENDING;
        }
        boolean anyOverdue = ledgerStatuses.stream().anyMatch(LedgerStatus.OVERDUE::equals);
        if (anyOverdue) {
            return FeesPaymentStatus.OVERDUE;
        }
        boolean allPaid = ledgerStatuses.stream().allMatch(LedgerStatus.PAID::equals);
        if (allPaid) {
            return FeesPaymentStatus.PAID;
        }
        boolean anyPartial = ledgerStatuses.stream().anyMatch(s -> LedgerStatus.PARTIAL.equals(s) || LedgerStatus.PAID.equals(s));
        if (anyPartial) {
            return FeesPaymentStatus.PARTIAL;
        }
        return FeesPaymentStatus.PENDING;
    }
}
