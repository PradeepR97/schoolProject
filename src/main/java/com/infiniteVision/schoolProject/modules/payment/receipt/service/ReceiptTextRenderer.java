package com.infiniteVision.schoolProject.modules.payment.receipt.service;

import com.infiniteVision.schoolProject.modules.payment.receipt.enums.PrinterType;
import com.infiniteVision.schoolProject.modules.payment.receipt.model.ReceiptFeeLine;
import com.infiniteVision.schoolProject.modules.payment.receipt.model.ReceiptPrintModel;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

/**
 * Renders receipt content as plain text for dot-matrix and thermal printers.
 */
@Component
public class ReceiptTextRenderer {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public String render(ReceiptPrintModel model, PrinterType printerType) {
        int width = resolveWidth(printerType);
        StringBuilder sb = new StringBuilder();
        appendCentered(sb, model.getSchoolName(), width);
        if (model.getSchoolAddress() != null && !model.getSchoolAddress().isBlank()) {
            appendCentered(sb, model.getSchoolAddress(), width);
        }
        if (model.getSchoolPhone() != null && !model.getSchoolPhone().isBlank()) {
            appendCentered(sb, "Ph: " + model.getSchoolPhone(), width);
        }
        appendLine(sb, repeat('=', width));
        appendCentered(sb, "FEE PAYMENT RECEIPT", width);
        if (model.isReprint()) {
            appendCentered(sb, "*** REPRINT ***", width);
        }
        appendLine(sb, "Copy: " + model.getCopyType());
        appendLine(sb, repeat('-', width));
        appendLine(sb, padLabel("Receipt No", model.getReceiptNo(), width));
        appendLine(sb, padLabel("Date", model.getPaymentDate().format(DATE_FMT), width));
        appendLine(sb, padLabel("Student", model.getStudentName(), width));
        appendLine(sb, padLabel("Admission", model.getAdmissionNo(), width));
        appendLine(sb, padLabel("Class", model.getClassName(), width));
        appendLine(sb, padLabel("Year", model.getAcademicYearLabel(), width));
        appendLine(sb, padLabel("Term", model.getTermLabel(), width));
        appendLine(sb, padLabel("Invoice", model.getInvoiceNo(), width));
        appendLine(sb, repeat('-', width));
        appendLine(sb, "Fee Breakdown");
        for (ReceiptFeeLine line : model.getFeeLines()) {
            appendLine(sb, padAmount(line.getLabel(), line.getAmount(), width));
        }
        appendLine(sb, repeat('-', width));
        appendLine(sb, padAmount("Gross", model.getGrossAmount(), width));
        appendLine(sb, padAmount("Discount", model.getDiscountAmount(), width));
        appendLine(sb, padAmount("Late Fee", model.getLateFee(), width));
        appendLine(sb, padAmount("Net", model.getNetAmount(), width));
        appendLine(sb, padAmount("Paid Now", model.getAmountPaid(), width));
        appendLine(sb, padAmount("Balance", model.getBalanceAfterPayment(), width));
        appendLine(sb, repeat('-', width));
        appendLine(sb, padLabel("Mode", model.getPaymentMode().name(), width));
        if (model.getTransactionRef() != null && !model.getTransactionRef().isBlank()) {
            appendLine(sb, padLabel("Txn Ref", model.getTransactionRef(), width));
        }
        appendLine(sb, padLabel("Collected By", model.getCollectedByName(), width));
        appendLine(sb, repeat('=', width));
        if (model.getFooterMessage() != null) {
            appendCentered(sb, model.getFooterMessage(), width);
        }
        appendLine(sb, "");
        appendLine(sb, padLabel(model.getSignatureLabel1(), "___________", width));
        appendLine(sb, padLabel(model.getSignatureLabel2(), "___________", width));
        if (printerType == PrinterType.DOT_MATRIX) {
            sb.append("\f");
        }
        return sb.toString();
    }

    private static int resolveWidth(PrinterType type) {
        return switch (type) {
            case THERMAL_58 -> 32;
            case THERMAL_80, DOT_MATRIX -> 48;
            default -> 48;
        };
    }

    private static void appendLine(StringBuilder sb, String line) {
        sb.append(line).append('\n');
    }

    private static void appendCentered(StringBuilder sb, String text, int width) {
        if (text == null) {
            return;
        }
        String trimmed = text.trim();
        if (trimmed.length() >= width) {
            appendLine(sb, trimmed.substring(0, width));
            return;
        }
        int pad = (width - trimmed.length()) / 2;
        appendLine(sb, " ".repeat(Math.max(0, pad)) + trimmed);
    }

    private static String padLabel(String label, String value, int width) {
        String content = label + ": " + (value != null ? value : "");
        return content.length() > width ? content.substring(0, width) : content;
    }

    private static String padAmount(String label, BigDecimal amount, int width) {
        String amt = amount != null ? amount.toPlainString() : "0.00";
        String line = label;
        int spaces = width - line.length() - amt.length();
        if (spaces < 1) {
            return (line + " " + amt).substring(0, width);
        }
        return line + " ".repeat(spaces) + amt;
    }

    private static String repeat(char c, int count) {
        return String.valueOf(c).repeat(count);
    }
}
