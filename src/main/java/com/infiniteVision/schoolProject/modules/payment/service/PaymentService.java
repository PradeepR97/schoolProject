package com.infiniteVision.schoolProject.modules.payment.service;

import com.infiniteVision.schoolProject.common.dto.response.PagedResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.request.CancelPaymentRequestDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.request.CollectPaymentRequestDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.request.RefundPaymentRequestDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.PaymentListItemResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.PaymentReceiptResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.StudentFeeDuesResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.enums.PaymentRecordStatus;
import java.time.LocalDate;

/**
 * Fee collection, listing, reversal, student dues inquiry, and receipt retrieval.
 */
public interface PaymentService {

    /**
     * Records a payment and updates ledger, invoice, and student fee status in one transaction.
     */
    PaymentReceiptResponseDTO collectPayment(CollectPaymentRequestDTO request);

    /**
     * Paginated payment list with optional filters.
     */
    PagedResponseDTO<PaymentListItemResponseDTO> listPayments(
            Long studentId,
            Long ledgerId,
            Long invoiceId,
            PaymentRecordStatus status,
            LocalDate fromDate,
            LocalDate toDate,
            int page,
            int size);

    /**
     * Cancels a successful payment and reverses ledger and invoice balances.
     */
    PaymentReceiptResponseDTO cancelPayment(Long paymentId, CancelPaymentRequestDTO request);

    /**
     * Refunds a successful payment and reverses ledger and invoice balances.
     */
    PaymentReceiptResponseDTO refundPayment(Long paymentId, RefundPaymentRequestDTO request);

    /**
     * Lists fee ledgers (and linked invoices) for a student.
     *
     * @param studentId student primary key
     * @param academicYearId optional filter; uses student's year when null
     */
    StudentFeeDuesResponseDTO getStudentDues(Long studentId, Long academicYearId);

    /**
     * Loads a payment receipt with related ledger and invoice amounts.
     */
    PaymentReceiptResponseDTO getReceiptById(Long paymentId);
}
