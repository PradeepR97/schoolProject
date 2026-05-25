package com.infiniteVision.schoolProject.modules.payment.receipt.repository;

import com.infiniteVision.schoolProject.modules.payment.receipt.entity.ReceiptPrintLog;
import com.infiniteVision.schoolProject.modules.payment.receipt.enums.PrintAction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptPrintLogRepository extends JpaRepository<ReceiptPrintLog, Long> {

    long countByPaymentId(Long paymentId);

    long countByPaymentIdAndPrintAction(Long paymentId, PrintAction printAction);

    List<ReceiptPrintLog> findAllByPaymentIdOrderByPrintedAtDesc(Long paymentId);
}
