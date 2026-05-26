package com.infiniteVision.schoolProject.modules.payment.receipt.service;

import com.infiniteVision.schoolProject.modules.academic.entity.AcademicYear;
import com.infiniteVision.schoolProject.modules.academic.entity.ClassMaster;
import com.infiniteVision.schoolProject.modules.academic.entity.SectionMaster;
import com.infiniteVision.schoolProject.modules.academic.repository.AcademicYearRepository;
import com.infiniteVision.schoolProject.modules.academic.repository.ClassMasterRepository;
import com.infiniteVision.schoolProject.modules.academic.repository.SectionMasterRepository;
import com.infiniteVision.schoolProject.modules.academic.util.ClassSectionDisplayFormatter;
import com.infiniteVision.schoolProject.modules.auth.entity.User;
import com.infiniteVision.schoolProject.modules.payment.entity.Invoice;
import com.infiniteVision.schoolProject.modules.payment.entity.Payment;
import com.infiniteVision.schoolProject.modules.payment.receipt.entity.SchoolPrintSettings;
import com.infiniteVision.schoolProject.modules.payment.receipt.enums.PrinterType;
import com.infiniteVision.schoolProject.modules.payment.receipt.enums.ReceiptCopyType;
import com.infiniteVision.schoolProject.modules.payment.receipt.model.ReceiptFeeLine;
import com.infiniteVision.schoolProject.modules.payment.receipt.model.ReceiptPrintModel;
import com.infiniteVision.schoolProject.modules.payment.receipt.repository.SchoolPrintSettingsRepository;
import com.infiniteVision.schoolProject.modules.student.entity.Student;
import com.infiniteVision.schoolProject.modules.student.enums.FeesPaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Builds a unified {@link ReceiptPrintModel} from payment, invoice, student, and school settings.
 */
@Component
@RequiredArgsConstructor
public class ReceiptDataAggregator {

    private static final long DEFAULT_SETTINGS_ID = 1L;

    private final SchoolPrintSettingsRepository schoolPrintSettingsRepository;
    private final ClassMasterRepository classMasterRepository;
    private final SectionMasterRepository sectionMasterRepository;
    private final AcademicYearRepository academicYearRepository;

    public ReceiptPrintModel build(
            Payment payment,
            ReceiptCopyType copyType,
            boolean reprint,
            int printSequence) {
        Student student = payment.getStudent();
        Invoice invoice = payment.getInvoice();
        SchoolPrintSettings settings = resolveSettings();

        String className = resolveClassName(
                invoice.getClassMaster() != null ? invoice.getClassMaster() : null,
                invoice.getSection() != null ? invoice.getSection() : null,
                student.getClassId(),
                student.getSectionId());
        String yearLabel = resolveYearLabel(invoice.getAcademicYear() != null
                ? invoice.getAcademicYear().getId()
                : student.getAcademicYearId());

        List<ReceiptFeeLine> feeLines = buildFeeLines(invoice);
        BigDecimal balanceAfter = invoice.getBalanceAmount() != null ? invoice.getBalanceAmount() : BigDecimal.ZERO;

        String qrPayload = payment.getReceiptNo()
                + "|"
                + payment.getId()
                + "|"
                + payment.getAmountPaid();

        return ReceiptPrintModel.builder()
                .paymentId(payment.getId())
                .receiptNo(payment.getReceiptNo())
                .paymentDate(payment.getPaymentDate())
                .paymentStatus(payment.getStatus())
                .paymentMode(payment.getPaymentMode())
                .transactionRef(payment.getTransactionRef())
                .chequeNo(payment.getChequeNo())
                .chequeDate(payment.getChequeDate())
                .bankName(payment.getBankName())
                .paymentRemarks(payment.getRemarks())
                .studentId(student.getId())
                .admissionNo(student.getAdmissionNo())
                .studentName(formatStudentName(student))
                .className(className)
                .academicYearLabel(yearLabel)
                .termLabel(invoice.getTerm() != null ? invoice.getTerm().name() : null)
                .invoiceNo(invoice.getInvoiceNo())
                .invoiceDate(invoice.getInvoiceDate())
                .dueDate(invoice.getDueDate())
                .feeLines(feeLines)
                .grossAmount(invoice.getGrossAmount())
                .discountAmount(invoice.getDiscountAmount())
                .lateFee(invoice.getLateFee())
                .netAmount(invoice.getNetAmount())
                .amountPaid(payment.getAmountPaid())
                .balanceAfterPayment(balanceAfter)
                .collectedByName(formatCollector(payment.getCollectedByUser()))
                .feesPaymentStatus(student.getFeesPaymentStatus() != null
                        ? student.getFeesPaymentStatus().name()
                        : FeesPaymentStatus.PENDING.name())
                .schoolName(settings.getSchoolName())
                .schoolAddress(settings.getSchoolAddress())
                .schoolPhone(settings.getSchoolPhone())
                .schoolEmail(settings.getSchoolEmail())
                .logoPath(settings.getLogoPath())
                .footerMessage(settings.getFooterMessage())
                .signatureLabel1(settings.getSignatureLabel1())
                .signatureLabel2(settings.getSignatureLabel2())
                .copyType(copyType)
                .reprint(reprint)
                .printSequence(printSequence)
                .qrPayload(qrPayload)
                .generatedAt(LocalDateTime.now())
                .build();
    }

    public SchoolPrintSettings resolveSettings() {
        return schoolPrintSettingsRepository
                .findById(DEFAULT_SETTINGS_ID)
                .orElseGet(this::defaultSettings);
    }

    private SchoolPrintSettings defaultSettings() {
        return SchoolPrintSettings.builder()
                .id(DEFAULT_SETTINGS_ID)
                .schoolName("School ERP")
                .schoolAddress("")
                .schoolPhone("")
                .footerMessage("Computer-generated receipt.")
                .signatureLabel1("Cashier")
                .signatureLabel2("Principal")
                .defaultPrinterType(PrinterType.LASER)
                .thermalWidthMm(80)
                .build();
    }

    private String resolveClassName(
            ClassMaster invoiceClass,
            SectionMaster invoiceSection,
            Long studentClassId,
            Long studentSectionId) {
        if (invoiceClass != null && invoiceSection != null) {
            return ClassSectionDisplayFormatter.format(invoiceClass, invoiceSection);
        }
        Long classId = studentClassId;
        Long sectionId = studentSectionId;
        if (classId == null) {
            return "";
        }
        ClassMaster classMaster = classMasterRepository.findById(classId).orElse(null);
        SectionMaster section =
                sectionId != null ? sectionMasterRepository.findById(sectionId).orElse(null) : null;
        return ClassSectionDisplayFormatter.format(classMaster, section);
    }

    private String resolveYearLabel(Long yearId) {
        if (yearId == null) {
            return "";
        }
        return academicYearRepository
                .findById(yearId)
                .map(AcademicYear::getYearName)
                .orElse("");
    }

    private static List<ReceiptFeeLine> buildFeeLines(Invoice invoice) {
        List<ReceiptFeeLine> lines = new ArrayList<>();
        addLine(lines, "Tuition Fee", invoice.getTuitionFee());
        addLine(lines, "Exam Fee", invoice.getExamFee());
        addLine(lines, "Lab Fee", invoice.getLabFee());
        addLine(lines, "Library Fee", invoice.getLibraryFee());
        addLine(lines, "Sports Fee", invoice.getSportsFee());
        addLine(lines, "Transport Fee", invoice.getTransportFee());
        addLine(lines, "Uniform Fee", invoice.getUniformFee());
        addLine(lines, "Misc Fee", invoice.getMiscFee());
        return lines;
    }

    private static void addLine(List<ReceiptFeeLine> lines, String label, BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
            lines.add(ReceiptFeeLine.builder().label(label).amount(amount).build());
        }
    }

    private static String formatStudentName(Student student) {
        String first = student.getFirstName() != null ? student.getFirstName().trim() : "";
        String last = student.getLastName() != null ? student.getLastName().trim() : "";
        return last.isEmpty() ? first : first + " " + last;
    }

    private static String formatCollector(User user) {
        if (user == null) {
            return "";
        }
        if (user.getFullName() != null && !user.getFullName().isBlank()) {
            return user.getFullName();
        }
        return user.getUsername() != null ? user.getUsername() : "";
    }
}
