package com.infiniteVision.schoolProject.modules.payment.repository;

import com.infiniteVision.schoolProject.modules.payment.entity.Invoice;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Persistence access for {@link Invoice} records.
 */
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByIdAndDeletedFalse(Long id);

    Optional<Invoice> findByInvoiceNoAndDeletedFalse(String invoiceNo);

    Optional<Invoice> findByLedger_IdAndDeletedFalse(Long ledgerId);

    List<Invoice> findAllByStudent_IdAndDeletedFalseOrderByInvoiceDateDesc(Long studentId);

    boolean existsByInvoiceNoAndDeletedFalse(String invoiceNo);

    @Query(
            """
            SELECT i FROM Invoice i
            JOIN FETCH i.student
            JOIN FETCH i.ledger
            JOIN FETCH i.classMaster
            JOIN FETCH i.academicYear
            WHERE i.id = :id AND i.deleted = false
            """)
    Optional<Invoice> findActiveWithRelationsById(@Param("id") Long id);

    @Query(
            """
            SELECT i FROM Invoice i
            WHERE i.ledger.id = :ledgerId AND i.deleted = false
            """)
    Optional<Invoice> findActiveWithLedgerByLedgerId(@Param("ledgerId") Long ledgerId);
}
