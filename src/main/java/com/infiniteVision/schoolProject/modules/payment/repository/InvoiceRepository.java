package com.infiniteVision.schoolProject.modules.payment.repository;

import com.infiniteVision.schoolProject.modules.payment.entity.Invoice;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
