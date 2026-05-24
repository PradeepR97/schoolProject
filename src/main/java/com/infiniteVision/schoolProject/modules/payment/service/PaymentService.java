package com.infiniteVision.schoolProject.modules.payment.service;

import com.infiniteVision.schoolProject.modules.payment.dto.request.CollectPaymentRequestDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.PaymentReceiptResponseDTO;
import com.infiniteVision.schoolProject.modules.payment.dto.response.StudentFeeDuesResponseDTO;

/**
 * Fee collection, student dues inquiry, and receipt retrieval.
 */
public interface PaymentService {

    /**
     * Records a payment and updates ledger, invoice, and student fee status in one transaction.
     */
    PaymentReceiptResponseDTO collectPayment(CollectPaymentRequestDTO request);

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
