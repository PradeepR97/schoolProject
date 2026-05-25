package com.infiniteVision.schoolProject.modules.payment.validator;

import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.exception.ValidationException;
import com.infiniteVision.schoolProject.modules.payment.dto.request.CollectPaymentRequestDTO;
import com.infiniteVision.schoolProject.modules.payment.entity.StudentFeeLedger;
import com.infiniteVision.schoolProject.modules.payment.enums.LedgerStatus;
import com.infiniteVision.schoolProject.modules.payment.enums.PaymentRecordStatus;
import com.infiniteVision.schoolProject.modules.student.entity.Student;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Business validation for payment collection and ledger generation prerequisites.
 */
@Component
public class PaymentValidator {

    public void validateStudentHasClassAndYear(Student student) {
        if (student.getClassId() == null || student.getAcademicYearId() == null) {
            throw new ValidationException(
                    MessageConstants.VALIDATION_FAILED, List.of(MessageConstants.STUDENT_CLASS_NOT_ASSIGNED));
        }
    }

    public void validateStudentYearMatches(Student student, Long academicYearId) {
        if (!student.getAcademicYearId().equals(academicYearId)) {
            throw new ValidationException(
                    MessageConstants.VALIDATION_FAILED,
                    List.of(MessageConstants.CLASS_ACADEMIC_YEAR_MISMATCH));
        }
    }

    public void validateCollectPayment(
            CollectPaymentRequestDTO request, Student student, StudentFeeLedger ledger) {
        if (!student.getId().equals(request.getStudentId())) {
            throw new ValidationException(
                    MessageConstants.VALIDATION_FAILED, List.of(MessageConstants.STUDENT_LEDGER_MISMATCH));
        }
        if (!ledger.getStudent().getId().equals(request.getStudentId())) {
            throw new ValidationException(
                    MessageConstants.VALIDATION_FAILED, List.of(MessageConstants.STUDENT_LEDGER_MISMATCH));
        }
        if (LedgerStatus.PAID.equals(ledger.getStatus())
                || ledger.getBalanceAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException(
                    MessageConstants.VALIDATION_FAILED, List.of(MessageConstants.FEE_LEDGER_ALREADY_PAID));
        }
        if (request.getAmountPaid().compareTo(ledger.getBalanceAmount()) > 0) {
            throw new ValidationException(
                    MessageConstants.VALIDATION_FAILED, List.of(MessageConstants.PAYMENT_AMOUNT_EXCEEDS_BALANCE));
        }
    }

    public void validatePaymentReversible(PaymentRecordStatus status) {
        if (PaymentRecordStatus.CANCELLED.equals(status)) {
            throw new ValidationException(
                    MessageConstants.VALIDATION_FAILED, List.of(MessageConstants.PAYMENT_ALREADY_CANCELLED));
        }
        if (PaymentRecordStatus.REFUNDED.equals(status)) {
            throw new ValidationException(
                    MessageConstants.VALIDATION_FAILED, List.of(MessageConstants.PAYMENT_ALREADY_REFUNDED));
        }
        if (!PaymentRecordStatus.SUCCESS.equals(status)) {
            throw new ValidationException(
                    MessageConstants.VALIDATION_FAILED, List.of(MessageConstants.PAYMENT_CANNOT_REVERSE));
        }
    }

    public void validateLateFeeAdjustment(StudentFeeLedger ledger, BigDecimal lateFee) {
        if (LedgerStatus.PAID.equals(ledger.getStatus())
                || ledger.getBalanceAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException(
                    MessageConstants.VALIDATION_FAILED, List.of(MessageConstants.FEE_LEDGER_CANNOT_ADJUST_LATE_FEE));
        }
        BigDecimal net = ledger.getActualAmount()
                .subtract(ledger.getDiscountAmount() != null ? ledger.getDiscountAmount() : BigDecimal.ZERO)
                .add(lateFee);
        if (ledger.getPaidAmount().compareTo(net) > 0) {
            throw new ValidationException(
                    MessageConstants.VALIDATION_FAILED, List.of(MessageConstants.FEE_LEDGER_PAID_EXCEEDS_NET));
        }
    }
}
