package com.infiniteVision.schoolProject.modules.reports.service;

import com.infiniteVision.schoolProject.modules.payment.entity.Payment;
import com.infiniteVision.schoolProject.modules.payment.entity.StudentFeeLedger;
import com.infiniteVision.schoolProject.modules.payment.enums.PaymentRecordStatus;
import com.infiniteVision.schoolProject.modules.reports.model.ReportDataset;
import com.infiniteVision.schoolProject.modules.reports.repository.ReportDataRepository;
import com.infiniteVision.schoolProject.modules.scholarship.entity.StudentScholarshipApplication;
import com.infiniteVision.schoolProject.modules.student.entity.Student;
import com.infiniteVision.schoolProject.modules.student.entity.StudentParent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Loads entities and builds tabular datasets for each report type.
 */
@Component
@RequiredArgsConstructor
public class ReportContentBuilder {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ReportDataRepository reportDataRepository;
    private final ReportClassLabelService reportClassLabelService;

    public ReportDataset buildStudentReport(Long academicYearId, Long classId) {
        List<Student> students = reportDataRepository.findStudentsForReport(academicYearId, classId);
        Map<Long, String> classNames = reportClassLabelService.loadClassNamesByAcademicYear(academicYearId);
        List<String> headers = List.of(
                "Admission No",
                "Class",
                "First Name",
                "Last Name",
                "Medium",
                "Gender",
                "Date of Birth",
                "Religion",
                "Community",
                "Blood Group",
                "Father Name",
                "Father Phone",
                "Mother Phone",
                "Primary Contact",
                "Status",
                "Fees Status");
        List<List<String>> rows = new ArrayList<>();
        for (Student student : students) {
            StudentParent parents = student.getParents();
            rows.add(List.of(
                    safe(student.getAdmissionNo()),
                    reportClassLabelService.resolveClassName(classNames, student.getClassId()),
                    safe(student.getFirstName()),
                    safe(student.getLastName()),
                    enumOrEmpty(student.getMedium()),
                    enumOrEmpty(student.getGender()),
                    formatDate(student.getDateOfBirth()),
                    enumOrEmpty(student.getReligion()),
                    enumOrEmpty(student.getCommunity()),
                    enumOrEmpty(student.getBloodGroup()),
                    parents != null ? safe(parents.getFatherName()) : "",
                    parents != null ? safe(parents.getFatherPhone()) : "",
                    parents != null ? safe(parents.getMotherPhone()) : "",
                    parents != null ? enumOrEmpty(parents.getPrimaryContact()) : "",
                    enumOrEmpty(student.getStatus()),
                    enumOrEmpty(student.getFeesPaymentStatus())));
        }
        String classFilterLabel = reportClassLabelService.resolveClassFilterTitle(academicYearId, classId);
        return ReportDataset.builder()
                .sheetName("Students")
                .reportTitle("Student Report - " + classFilterLabel)
                .headers(headers)
                .rows(rows)
                .build();
    }

    public ReportDataset buildFeeCollectionReport(
            Long academicYearId, Long classId, YearMonth rangeStart, YearMonth rangeEnd) {
        LocalDate fromDate = rangeStart.atDay(1);
        LocalDate toDate = rangeEnd.atEndOfMonth();
        List<Payment> payments = reportDataRepository.findPaymentsForFeeCollectionReport(
                academicYearId, classId, PaymentRecordStatus.SUCCESS, fromDate, toDate);
        Map<Long, String> classNames = reportClassLabelService.loadClassNamesByAcademicYear(academicYearId);
        List<String> headers = List.of(
                "Payment Date",
                "Receipt No",
                "Admission No",
                "Student Name",
                "Class",
                "Amount Paid",
                "Payment Mode",
                "Invoice No");
        List<List<String>> rows = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (Payment payment : payments) {
            Student student = payment.getStudent();
            BigDecimal amount = payment.getAmountPaid() != null ? payment.getAmountPaid() : BigDecimal.ZERO;
            total = total.add(amount);
            rows.add(List.of(
                    formatDate(payment.getPaymentDate()),
                    safe(payment.getReceiptNo()),
                    student != null ? safe(student.getAdmissionNo()) : "",
                    student != null ? formatStudentName(student) : "",
                    student != null
                            ? reportClassLabelService.resolveClassName(classNames, student.getClassId())
                            : "",
                    amount.toPlainString(),
                    enumOrEmpty(payment.getPaymentMode()),
                    payment.getInvoice() != null ? safe(payment.getInvoice().getInvoiceNo()) : ""));
        }
        rows.add(List.of("", "", "", "", "TOTAL", total.toPlainString(), "", ""));
        return ReportDataset.builder()
                .sheetName("Fee Collection")
                .reportTitle(formatRangeReportTitle("Fee Collection Report", rangeStart, rangeEnd))
                .headers(headers)
                .rows(rows)
                .build();
    }

    public ReportDataset buildPendingFeesReport(
            Long academicYearId, Long classId, YearMonth rangeStart, YearMonth rangeEnd) {
        LocalDate fromDueDate = rangeStart.atDay(1);
        LocalDate toDueDate = rangeEnd.atEndOfMonth();
        List<StudentFeeLedger> ledgers = reportDataRepository.findPendingFeesForReport(
                academicYearId, classId, fromDueDate, toDueDate);
        Map<Long, String> classNames = reportClassLabelService.loadClassNamesByAcademicYear(academicYearId);
        List<String> headers = List.of(
                "Admission No",
                "Student Name",
                "Class",
                "Fee Head",
                "Term",
                "Net Amount",
                "Paid Amount",
                "Balance",
                "Due Date",
                "Status",
                "Contact Name",
                "Contact Phone");
        List<List<String>> rows = new ArrayList<>();
        for (StudentFeeLedger ledger : ledgers) {
            Student student = ledger.getStudent();
            StudentParent parents = student != null ? student.getParents() : null;
            String feeHeadName =
                    ledger.getFeeStructure() != null && ledger.getFeeStructure().getFeeHead() != null
                            ? ledger.getFeeStructure().getFeeHead().getFeeHeadName()
                            : "";
            rows.add(List.of(
                    student != null ? safe(student.getAdmissionNo()) : "",
                    student != null ? formatStudentName(student) : "",
                    student != null
                            ? reportClassLabelService.resolveClassName(classNames, student.getClassId())
                            : "",
                    feeHeadName,
                    enumOrEmpty(ledger.getTerm()),
                    decimalOrEmpty(ledger.getNetAmount()),
                    decimalOrEmpty(ledger.getPaidAmount()),
                    decimalOrEmpty(ledger.getBalanceAmount()),
                    formatDate(ledger.getDueDate()),
                    enumOrEmpty(ledger.getStatus()),
                    resolveContactName(parents),
                    resolveContactPhone(parents)));
        }
        return ReportDataset.builder()
                .sheetName("Pending Fees")
                .reportTitle(formatRangeReportTitle("Pending Fees Report", rangeStart, rangeEnd))
                .headers(headers)
                .rows(rows)
                .build();
    }

    public ReportDataset buildScholarshipReport(
            Long academicYearId, Long classId, YearMonth rangeStart, YearMonth rangeEnd) {
        LocalDateTime fromAppliedAt = rangeStart.atDay(1).atStartOfDay();
        LocalDateTime toAppliedAt = rangeEnd.atEndOfMonth().atTime(LocalTime.MAX);
        List<StudentScholarshipApplication> applications = reportDataRepository.findScholarshipApplicationsForReport(
                academicYearId, classId, fromAppliedAt, toAppliedAt);
        Map<Long, String> classNames = reportClassLabelService.loadClassNamesByAcademicYear(academicYearId);
        List<String> headers = List.of(
                "Application Id",
                "Admission No",
                "Student Name",
                "Class",
                "Scheme Name",
                "Status",
                "Applied At",
                "Principal Approved At",
                "Correspondent Approved At",
                "Rejection Reason");
        List<List<String>> rows = new ArrayList<>();
        for (StudentScholarshipApplication application : applications) {
            Student student = application.getStudent();
            rows.add(List.of(
                    application.getId() != null ? application.getId().toString() : "",
                    student != null ? safe(student.getAdmissionNo()) : "",
                    student != null ? formatStudentName(student) : "",
                    student != null
                            ? reportClassLabelService.resolveClassName(classNames, student.getClassId())
                            : "",
                    application.getScheme() != null ? safe(application.getScheme().getSchemeName()) : "",
                    enumOrEmpty(application.getStatus()),
                    formatDateTime(application.getAppliedAt()),
                    formatDateTime(application.getPrincipalApprovedAt()),
                    formatDateTime(application.getCorrespondentApprovedAt()),
                    safe(application.getRejectionReason())));
        }
        return ReportDataset.builder()
                .sheetName("Scholarships")
                .reportTitle(formatRangeReportTitle("Scholarship Report", rangeStart, rangeEnd))
                .headers(headers)
                .rows(rows)
                .build();
    }

    private static String resolveContactName(StudentParent parents) {
        if (parents == null || parents.getPrimaryContact() == null) {
            return "";
        }
        return switch (parents.getPrimaryContact()) {
            case FATHER -> safe(parents.getFatherName());
            case MOTHER -> safe(parents.getMotherName());
            case GUARDIAN -> safe(parents.getGuardianName());
        };
    }

    private static String resolveContactPhone(StudentParent parents) {
        if (parents == null || parents.getPrimaryContact() == null) {
            return "";
        }
        return switch (parents.getPrimaryContact()) {
            case FATHER -> safe(parents.getFatherPhone());
            case MOTHER -> safe(parents.getMotherPhone());
            case GUARDIAN -> safe(parents.getGuardianPhone());
        };
    }

    private static String formatStudentName(Student student) {
        String last = student.getLastName() != null ? student.getLastName().trim() : "";
        return last.isEmpty() ? student.getFirstName().trim() : student.getFirstName().trim() + " " + last;
    }

    private static String safe(String value) {
        return value != null ? value : "";
    }

    private static String enumOrEmpty(Enum<?> value) {
        return value != null ? value.name() : "";
    }

    private static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMAT) : "";
    }

    private static String formatDateTime(java.time.LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_TIME_FORMAT) : "";
    }

    private static String decimalOrEmpty(BigDecimal value) {
        return value != null ? value.toPlainString() : "";
    }

    private static String formatRangeReportTitle(String titlePrefix, YearMonth rangeStart, YearMonth rangeEnd) {
        if (rangeStart.equals(rangeEnd)) {
            return titlePrefix + " - " + rangeStart;
        }
        return titlePrefix + " - " + rangeStart + " to " + rangeEnd;
    }
}
