package com.infiniteVision.schoolProject.modules.payment.service;

import com.infiniteVision.schoolProject.common.dto.response.PagedResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.request.GenerateInvoiceRequestDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.InvoiceDetailResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.InvoiceListItemResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.InvoiceSummaryResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.enums.InvoiceStatus;

/**
 * Fee invoice generation and paginated listing.
 */
public interface InvoiceService {

    /**
     * Issues one invoice for a ledger (unique per ledger).
     *
     * @param request ledger id
     * @return invoice summary
     */
    InvoiceSummaryResponseDTO generateInvoice(GenerateInvoiceRequestDTO request);

    /**
     * Paginated invoice list with optional filters.
     */
    PagedResponseDTO<InvoiceListItemResponseDTO> listInvoices(
            Long studentId, Long academicYearId, InvoiceStatus status, int page, int size);

    /**
     * Loads full invoice detail by primary key.
     */
    InvoiceDetailResponseDTO getInvoiceById(Long invoiceId);
}
