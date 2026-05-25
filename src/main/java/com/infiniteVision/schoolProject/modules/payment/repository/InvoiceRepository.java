package com.infiniteVision.schoolProject.modules.payment.repository;

import com.infiniteVision.schoolProject.modules.payment.entity.Invoice;
import com.infiniteVision.schoolProject.modules.payment.enums.InvoiceStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query(
            value = """
                    SELECT i FROM Invoice i
                    JOIN FETCH i.student s
                    JOIN FETCH i.ledger
                    JOIN FETCH i.classMaster
                    JOIN FETCH i.academicYear ay
                    WHERE i.deleted = false
                    AND (:studentId IS NULL OR s.id = :studentId)
                    AND (:academicYearId IS NULL OR ay.id = :academicYearId)
                    AND (:status IS NULL OR i.status = :status)
                    """,
            countQuery = """
                    SELECT COUNT(i) FROM Invoice i
                    WHERE i.deleted = false
                    AND (:studentId IS NULL OR i.student.id = :studentId)
                    AND (:academicYearId IS NULL OR i.academicYear.id = :academicYearId)
                    AND (:status IS NULL OR i.status = :status)
                    """)
    Page<Invoice> findAllFiltered(
            @Param("studentId") Long studentId,
            @Param("academicYearId") Long academicYearId,
            @Param("status") InvoiceStatus status,
            Pageable pageable);

    long count();
}
