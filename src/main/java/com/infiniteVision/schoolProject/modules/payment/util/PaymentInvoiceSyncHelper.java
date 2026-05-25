package com.infiniteVision.schoolProject.modules.payment.util;

import com.infiniteVision.schoolProject.modules.payment.entity.Invoice;
import com.infiniteVision.schoolProject.modules.payment.entity.StudentFeeLedger;
import com.infiniteVision.schoolProject.modules.payment.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Keeps invoice amounts and status aligned with a fee ledger row after ledger changes.
 */
@Component
@RequiredArgsConstructor
public class PaymentInvoiceSyncHelper {

    private final InvoiceRepository invoiceRepository;

    public void syncInvoiceFromLedger(StudentFeeLedger ledger) {
        invoiceRepository.findByLedger_IdAndDeletedFalse(ledger.getId()).ifPresent(invoice -> {
            invoice.setDiscountAmount(ledger.getDiscountAmount());
            invoice.setLateFee(ledger.getLateFee());
            invoice.setNetAmount(ledger.getNetAmount());
            invoice.setPaidAmount(ledger.getPaidAmount());
            invoice.setBalanceAmount(ledger.getBalanceAmount());
            invoice.setStatus(PaymentStatusCalculator.resolveInvoiceStatus(
                    ledger.getBalanceAmount(),
                    ledger.getPaidAmount(),
                    invoice.getDueDate(),
                    invoice.getStatus()));
            invoiceRepository.save(invoice);
        });
    }

    public Invoice findInvoiceForLedger(StudentFeeLedger ledger) {
        return invoiceRepository.findByLedger_IdAndDeletedFalse(ledger.getId()).orElse(null);
    }
}
