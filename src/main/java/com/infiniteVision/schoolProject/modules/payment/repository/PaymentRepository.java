package com.infiniteVision.schoolProject.modules.payment.repository;

import com.infiniteVision.schoolProject.modules.payment.entity.Payment;
import java.util.List;
import java.util.Optional;
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
}
