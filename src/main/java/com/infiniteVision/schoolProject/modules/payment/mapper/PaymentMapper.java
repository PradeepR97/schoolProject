package com.infiniteVision.schoolProject.modules.payment.mapper;

import com.infiniteVision.schoolProject.modules.fees.entity.FeeHead;
import com.infiniteVision.schoolProject.modules.payment.dto.response.FeeLedgerSummaryResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.InvoiceSummaryResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.PaymentReceiptResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.StudentFeeDueItemResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.entity.Invoice;
import com.infiniteVision.schoolProject.modules.payment.entity.Payment;
import com.infiniteVision.schoolProject.modules.payment.entity.StudentFeeLedger;
import com.infiniteVision.schoolProject.modules.payment.enums.InvoiceStatus;
import com.infiniteVision.schoolProject.modules.payment.enums.LedgerStatus;
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
