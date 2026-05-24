package com.infiniteVision.schoolProject.modules.payment.service;

import com.infiniteVision.schoolProject.modules.payment.dto.request.GenerateInvoiceRequestDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.InvoiceSummaryResponseDTO;

/**
 * Fee invoice generation from student fee ledgers.
 */
public interface InvoiceService {

    /**
     * Issues one invoice for a ledger (unique per ledger).
     *
     * @param request ledger id
     * @return invoice summary
     */
    InvoiceSummaryResponseDTO generateInvoice(GenerateInvoiceRequestDTO request);
}
