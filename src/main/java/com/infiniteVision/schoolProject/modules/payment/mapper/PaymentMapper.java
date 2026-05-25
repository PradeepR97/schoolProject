package com.infiniteVision.schoolProject.modules.payment.mapper;

import com.infiniteVision.schoolProject.modules.fees.entity.FeeHead;
import com.infiniteVision.schoolProject.modules.payment.dto.response.FeeLedgerDetailResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.FeeLedgerSummaryResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.InvoiceDetailResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.InvoiceListItemResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.InvoiceSummaryResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.PaymentListItemResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.PaymentReceiptResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.StudentFeeDueItemResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.entity.Invoice;
import com.infiniteVision.schoolProject.modules.payment.entity.Payment;
import com.infiniteVision.schoolProject.modules.payment.entity.StudentFeeLedger;
import com.infiniteVision.schoolProject.modules.payment.enums.InvoiceStatus;
import com.infiniteVision.schoolProject.modules.payment.enums.LedgerStatus;
import java.time.LocalDate;
import com.infiniteVision.schoolProject.modules.scholarship.enums.ScholarshipApplicationStatus;
import com.infiniteVision.schoolProject.modules.student.entity.Student;
import com.infiniteVision.schoolProject.modules.student.enums.FeesPaymentStatus;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

/**
 * Maps payment entities to API response DTOs.
 */
@Component
public class PaymentMapper {

    public FeeLedgerSummaryResponseDTO toLedgerSummary(StudentFeeLedger ledger) {
        FeeHead feeHead = ledger.getFeeStructure() != null ? ledger.getFeeStructure().getFeeHead() : null;
        return FeeLedgerSummaryResponseDTO.builder()
                .ledgerId(ledger.getId())
                .structureId(ledger.getFeeStructure() != null ? ledger.getFeeStructure().getId() : null)
                .feeHeadName(feeHead != null ? feeHead.getFeeHeadName() : null)
                .term(ledger.getTerm())
                .netAmount(ledger.getNetAmount())
                .paidAmount(ledger.getPaidAmount())
                .balanceAmount(ledger.getBalanceAmount())
                .dueDate(ledger.getDueDate())
                .status(ledger.getStatus())
                .build();
    }

    public InvoiceSummaryResponseDTO toInvoiceSummary(Invoice invoice) {
        return InvoiceSummaryResponseDTO.builder()
                .invoiceId(invoice.getId())
                .invoiceNo(invoice.getInvoiceNo())
                .term(invoice.getTerm())
                .invoiceDate(invoice.getInvoiceDate())
                .dueDate(invoice.getDueDate())
                .netAmount(invoice.getNetAmount())
                .paidAmount(invoice.getPaidAmount())
                .balanceAmount(invoice.getBalanceAmount())
                .status(invoice.getStatus())
                .build();
    }

    public StudentFeeDueItemResponseDTO toDueItem(StudentFeeLedger ledger, Invoice invoice) {
        return toDueItem(ledger, invoice, null, null);
    }

    public StudentFeeDueItemResponseDTO toDueItem(
            StudentFeeLedger ledger,
            Invoice invoice,
            BigDecimal pendingScholarshipDiscount,
            ScholarshipApplicationStatus pendingScholarshipStatus) {
        FeeHead feeHead = ledger.getFeeStructure() != null ? ledger.getFeeStructure().getFeeHead() : null;
        return StudentFeeDueItemResponseDTO.builder()
                .ledgerId(ledger.getId())
                .structureId(ledger.getFeeStructure() != null ? ledger.getFeeStructure().getId() : null)
                .feeHeadName(feeHead != null ? feeHead.getFeeHeadName() : null)
                .term(ledger.getTerm())
                .netAmount(ledger.getNetAmount())
                .paidAmount(ledger.getPaidAmount())
                .balanceAmount(ledger.getBalanceAmount())
                .dueDate(ledger.getDueDate())
                .status(ledger.getStatus())
                .invoiceId(invoice != null ? invoice.getId() : null)
                .invoiceNo(invoice != null ? invoice.getInvoiceNo() : null)
                .invoiceStatus(invoice != null ? invoice.getStatus() : null)
                .pendingScholarshipDiscount(pendingScholarshipDiscount)
                .pendingScholarshipStatus(pendingScholarshipStatus)
                .build();
    }

    public FeeLedgerDetailResponseDTO toLedgerDetail(
            StudentFeeLedger ledger,
            BigDecimal pendingScholarshipDiscount,
            ScholarshipApplicationStatus pendingScholarshipStatus) {
        FeeHead feeHead = ledger.getFeeStructure() != null ? ledger.getFeeStructure().getFeeHead() : null;
        return FeeLedgerDetailResponseDTO.builder()
                .ledgerId(ledger.getId())
                .studentId(ledger.getStudent() != null ? ledger.getStudent().getId() : null)
                .academicYearId(
                        ledger.getAcademicYear() != null ? ledger.getAcademicYear().getId() : null)
                .structureId(ledger.getFeeStructure() != null ? ledger.getFeeStructure().getId() : null)
                .feeHeadName(feeHead != null ? feeHead.getFeeHeadName() : null)
                .term(ledger.getTerm())
                .actualAmount(ledger.getActualAmount())
                .discountAmount(ledger.getDiscountAmount())
                .lateFee(ledger.getLateFee())
                .netAmount(ledger.getNetAmount())
                .paidAmount(ledger.getPaidAmount())
                .balanceAmount(ledger.getBalanceAmount())
                .dueDate(ledger.getDueDate())
                .status(ledger.getStatus())
                .pendingScholarshipDiscount(pendingScholarshipDiscount)
                .pendingScholarshipStatus(pendingScholarshipStatus)
                .build();
    }

    public InvoiceDetailResponseDTO toInvoiceDetail(Invoice invoice) {
        Student student = invoice.getStudent();
        return InvoiceDetailResponseDTO.builder()
                .invoiceId(invoice.getId())
                .invoiceNo(invoice.getInvoiceNo())
                .studentId(student != null ? student.getId() : null)
                .admissionNo(student != null ? student.getAdmissionNo() : null)
                .studentName(formatStudentName(student))
                .ledgerId(invoice.getLedger() != null ? invoice.getLedger().getId() : null)
                .academicYearId(
                        invoice.getAcademicYear() != null ? invoice.getAcademicYear().getId() : null)
                .term(invoice.getTerm())
                .invoiceDate(invoice.getInvoiceDate())
                .dueDate(invoice.getDueDate())
                .grossAmount(invoice.getGrossAmount())
                .discountAmount(invoice.getDiscountAmount())
                .lateFee(invoice.getLateFee())
                .netAmount(invoice.getNetAmount())
                .paidAmount(invoice.getPaidAmount())
                .balanceAmount(invoice.getBalanceAmount())
                .status(invoice.getStatus())
                .build();
    }

    public PaymentReceiptResponseDTO toReceiptResponse(
            Payment payment,
            StudentFeeLedger ledger,
            Invoice invoice,
            FeesPaymentStatus studentFeesStatus) {
        return PaymentReceiptResponseDTO.builder()
                .paymentId(payment.getId())
                .receiptNo(payment.getReceiptNo())
                .studentId(payment.getStudent() != null ? payment.getStudent().getId() : null)
                .ledgerId(ledger.getId())
                .invoiceId(invoice.getId())
                .amountPaid(payment.getAmountPaid())
                .paymentMode(payment.getPaymentMode())
                .paymentDate(payment.getPaymentDate())
                .status(payment.getStatus())
                .ledgerPaidAmount(ledger.getPaidAmount())
                .ledgerBalanceAmount(ledger.getBalanceAmount())
                .ledgerStatus(ledger.getStatus())
                .invoiceStatus(invoice.getStatus())
                .studentFeesPaymentStatus(studentFeesStatus)
                .build();
    }

    public PaymentListItemResponseDTO toPaymentListItem(Payment payment) {
        Student student = payment.getStudent();
        return PaymentListItemResponseDTO.builder()
                .paymentId(payment.getId())
                .receiptNo(payment.getReceiptNo())
                .studentId(student != null ? student.getId() : null)
                .admissionNo(student != null ? student.getAdmissionNo() : null)
                .studentName(formatStudentName(student))
                .ledgerId(payment.getLedger() != null ? payment.getLedger().getId() : null)
                .invoiceId(payment.getInvoice() != null ? payment.getInvoice().getId() : null)
                .invoiceNo(payment.getInvoice() != null ? payment.getInvoice().getInvoiceNo() : null)
                .amountPaid(payment.getAmountPaid())
                .paymentMode(payment.getPaymentMode())
                .paymentDate(payment.getPaymentDate())
                .status(payment.getStatus())
                .collectedByUsername(
                        payment.getCollectedByUser() != null ? payment.getCollectedByUser().getUsername() : null)
                .build();
    }

    public InvoiceListItemResponseDTO toInvoiceListItem(Invoice invoice) {
        Student student = invoice.getStudent();
        return InvoiceListItemResponseDTO.builder()
                .invoiceId(invoice.getId())
                .invoiceNo(invoice.getInvoiceNo())
                .studentId(student != null ? student.getId() : null)
                .admissionNo(student != null ? student.getAdmissionNo() : null)
                .studentName(formatStudentName(student))
                .ledgerId(invoice.getLedger() != null ? invoice.getLedger().getId() : null)
                .term(invoice.getTerm())
                .invoiceDate(invoice.getInvoiceDate())
                .dueDate(invoice.getDueDate())
                .netAmount(invoice.getNetAmount())
                .paidAmount(invoice.getPaidAmount())
                .balanceAmount(invoice.getBalanceAmount())
                .status(invoice.getStatus())
                .build();
    }

    private String formatStudentName(Student student) {
        if (student == null) {
            return null;
        }
        String name = student.getFirstName()
                + (student.getLastName() != null && !student.getLastName().isBlank()
                        ? " " + student.getLastName()
                        : "");
        return name.trim();
    }

    /**
     * Applies fee head code to the matching invoice line column; remainder goes to {@code misc_fee}.
     */
    public void applyFeeHeadToInvoiceLine(Invoice invoice, String feeHeadCode, BigDecimal amount) {
        if (feeHeadCode == null || amount == null) {
            return;
        }
        String code = feeHeadCode.toUpperCase();
        if (code.contains("TUITION")) {
            invoice.setTuitionFee(invoice.getTuitionFee().add(amount));
        } else if (code.contains("EXAM")) {
            invoice.setExamFee(invoice.getExamFee().add(amount));
        } else if (code.contains("LAB")) {
            invoice.setLabFee(invoice.getLabFee().add(amount));
        } else if (code.contains("LIBRARY")) {
            invoice.setLibraryFee(invoice.getLibraryFee().add(amount));
        } else if (code.contains("SPORT")) {
            invoice.setSportsFee(invoice.getSportsFee().add(amount));
        } else if (code.contains("TRANSPORT")) {
            invoice.setTransportFee(invoice.getTransportFee().add(amount));
        } else if (code.contains("UNIFORM")) {
            invoice.setUniformFee(invoice.getUniformFee().add(amount));
        } else {
            invoice.setMiscFee(invoice.getMiscFee().add(amount));
        }
    }

    private static boolean isOverdue(StudentFeeLedger ledger) {
        if (ledger.getStatus() == LedgerStatus.OVERDUE) {
            return true;
        }
        if (ledger.getBalanceAmount() == null
                || ledger.getBalanceAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        return ledger.getDueDate() != null && ledger.getDueDate().isBefore(LocalDate.now());
    }

    public void recalculateInvoiceTotals(Invoice invoice) {
        BigDecimal gross = invoice.getTuitionFee()
                .add(invoice.getExamFee())
                .add(invoice.getLabFee())
                .add(invoice.getLibraryFee())
                .add(invoice.getSportsFee())
                .add(invoice.getTransportFee())
                .add(invoice.getUniformFee())
                .add(invoice.getMiscFee());
        invoice.setGrossAmount(gross);
        invoice.setNetAmount(gross.subtract(invoice.getDiscountAmount()).add(invoice.getLateFee()));
        invoice.setBalanceAmount(invoice.getNetAmount().subtract(invoice.getPaidAmount()));
    }

    public InvoiceStatus defaultNewInvoiceStatus() {
        return InvoiceStatus.SENT;
    }

    public LedgerStatus defaultNewLedgerStatus() {
        return LedgerStatus.PENDING;
    }
}
