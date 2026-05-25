package com.infiniteVision.schoolProject.modules.payment.repository;

import com.infiniteVision.schoolProject.modules.payment.enums.PaymentRecordStatus;
import com.infiniteVision.schoolProject.modules.payment.entity.Payment;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Persistence access for {@link Payment} (fee receipt) records.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByIdAndDeletedFalse(Long id);

    Optional<Payment> findByReceiptNoAndDeletedFalse(String receiptNo);

    List<Payment> findAllByStudent_IdAndDeletedFalseOrderByPaymentDateDesc(Long studentId);

    List<Payment> findAllByInvoice_IdAndDeletedFalseOrderByPaymentDateDesc(Long invoiceId);

    List<Payment> findAllByLedger_IdAndDeletedFalseOrderByPaymentDateDesc(Long ledgerId);

    boolean existsByReceiptNoAndDeletedFalse(String receiptNo);

    @Query(
            """
            SELECT p FROM Payment p
            JOIN FETCH p.student
            JOIN FETCH p.ledger
            JOIN FETCH p.invoice
            LEFT JOIN FETCH p.collectedByUser
            WHERE p.id = :id AND p.deleted = false
            """)
    Optional<Payment> findActiveWithRelationsById(@Param("id") Long id);

    long countByDeletedFalse();

    Optional<Payment> findByIdempotencyKeyAndDeletedFalse(String idempotencyKey);

    boolean existsByPaymentBatchIdAndDeletedFalse(String paymentBatchId);

    @Query(
            """
            SELECT p FROM Payment p
            JOIN FETCH p.student
            JOIN FETCH p.ledger l
            JOIN FETCH l.feeStructure fs
            JOIN FETCH fs.feeHead
            JOIN FETCH p.invoice
            LEFT JOIN FETCH p.collectedByUser
            WHERE p.paymentBatchId = :paymentBatchId AND p.deleted = false
            ORDER BY p.id ASC
            """)
    List<Payment> findAllActiveWithRelationsByPaymentBatchId(@Param("paymentBatchId") String paymentBatchId);

    @Query(
            """
            SELECT p FROM Payment p
            JOIN FETCH p.student
            JOIN FETCH p.ledger
            JOIN FETCH p.invoice
            LEFT JOIN FETCH p.collectedByUser
            WHERE p.idempotencyKey = :idempotencyKey AND p.deleted = false
            """)
    Optional<Payment> findActiveWithRelationsByIdempotencyKey(@Param("idempotencyKey") String idempotencyKey);

    @Query(
            value = """
                    SELECT p FROM Payment p
                    JOIN FETCH p.student s
                    JOIN FETCH p.ledger
                    JOIN FETCH p.invoice i
                    LEFT JOIN FETCH p.collectedByUser
                    WHERE p.deleted = false
                    AND (:studentId IS NULL OR s.id = :studentId)
                    AND (:ledgerId IS NULL OR p.ledger.id = :ledgerId)
                    AND (:invoiceId IS NULL OR i.id = :invoiceId)
                    AND (:status IS NULL OR p.status = :status)
                    AND (:fromDate IS NULL OR p.paymentDate >= :fromDate)
                    AND (:toDate IS NULL OR p.paymentDate <= :toDate)
                    """,
            countQuery = """
                    SELECT COUNT(p) FROM Payment p
                    WHERE p.deleted = false
                    AND (:studentId IS NULL OR p.student.id = :studentId)
                    AND (:ledgerId IS NULL OR p.ledger.id = :ledgerId)
                    AND (:invoiceId IS NULL OR p.invoice.id = :invoiceId)
                    AND (:status IS NULL OR p.status = :status)
                    AND (:fromDate IS NULL OR p.paymentDate >= :fromDate)
                    AND (:toDate IS NULL OR p.paymentDate <= :toDate)
                    """)
    Page<Payment> findAllFiltered(
            @Param("studentId") Long studentId,
            @Param("ledgerId") Long ledgerId,
            @Param("invoiceId") Long invoiceId,
            @Param("status") PaymentRecordStatus status,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable);
}
