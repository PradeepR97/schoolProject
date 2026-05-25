package com.infiniteVision.schoolProject.modules.payment.receipt.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.infiniteVision.schoolProject.modules.payment.receipt.config.ReceiptProperties;
import com.infiniteVision.schoolProject.modules.payment.receipt.model.ReceiptFeeLine;
import com.infiniteVision.schoolProject.modules.payment.receipt.model.ReceiptPrintModel;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Generates A4 laser PDF receipts using OpenPDF.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReceiptPdfGenerator {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final ReceiptProperties receiptProperties;

    /**
     * Writes PDF bytes to the given path and returns the path.
     */
    public Path generateAndSave(ReceiptPrintModel model, Path targetFile) throws IOException {
        byte[] pdfBytes = generateBytes(model);
        Files.createDirectories(targetFile.getParent());
        Files.write(targetFile, pdfBytes);
        return targetFile;
    }

    public byte[] generateBytes(ReceiptPrintModel model) throws IOException {
        Document document = new Document(PageSize.A4, 36, 36, 48, 36);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();

        if (model.isReprint()) {
            addReprintWatermark(writer, document);
        }

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font normal = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Font small = FontFactory.getFont(FontFactory.HELVETICA, 9);

        Paragraph school = new Paragraph(model.getSchoolName(), titleFont);
        school.setAlignment(Element.ALIGN_CENTER);
        document.add(school);

        if (model.getSchoolAddress() != null && !model.getSchoolAddress().isBlank()) {
            Paragraph addr = new Paragraph(model.getSchoolAddress(), small);
            addr.setAlignment(Element.ALIGN_CENTER);
            document.add(addr);
        }
        if (model.getSchoolPhone() != null && !model.getSchoolPhone().isBlank()) {
            Paragraph phone = new Paragraph("Phone: " + model.getSchoolPhone(), small);
            phone.setAlignment(Element.ALIGN_CENTER);
            document.add(phone);
        }

        Paragraph receiptTitle = new Paragraph("FEE PAYMENT RECEIPT", headerFont);
        receiptTitle.setAlignment(Element.ALIGN_CENTER);
        receiptTitle.setSpacingBefore(8f);
        document.add(receiptTitle);

        Paragraph copy = new Paragraph("Copy: " + model.getCopyType(), normal);
        copy.setAlignment(Element.ALIGN_CENTER);
        document.add(copy);

        document.add(new Paragraph(" "));

        PdfPTable meta = new PdfPTable(2);
        meta.setWidthPercentage(100);
        addMetaRow(meta, "Receipt No", model.getReceiptNo(), normal);
        addMetaRow(meta, "Payment Date", model.getPaymentDate().format(DATE_FMT), normal);
        addMetaRow(meta, "Student", model.getStudentName(), normal);
        addMetaRow(meta, "Admission No", model.getAdmissionNo(), normal);
        addMetaRow(meta, "Class", model.getClassName(), normal);
        addMetaRow(meta, "Academic Year", model.getAcademicYearLabel(), normal);
        addMetaRow(meta, "Term", model.getTermLabel(), normal);
        addMetaRow(meta, "Invoice No", model.getInvoiceNo(), normal);
        document.add(meta);

        document.add(new Paragraph("Fee Breakdown", headerFont));

        PdfPTable fees = new PdfPTable(2);
        fees.setWidthPercentage(100);
        fees.addCell(headerCell("Description", normal));
        fees.addCell(headerCell("Amount (Rs.)", normal));
        for (ReceiptFeeLine line : model.getFeeLines()) {
            fees.addCell(bodyCell(line.getLabel(), normal));
            fees.addCell(bodyCell(formatMoney(line.getAmount()), normal));
        }
        document.add(fees);

        PdfPTable totals = new PdfPTable(2);
        totals.setWidthPercentage(60);
        totals.setHorizontalAlignment(Element.ALIGN_RIGHT);
        addTotalRow(totals, "Gross", model.getGrossAmount(), normal);
        addTotalRow(totals, "Discount", model.getDiscountAmount(), normal);
        addTotalRow(totals, "Late Fee", model.getLateFee(), normal);
        addTotalRow(totals, "Net Amount", model.getNetAmount(), normal);
        addTotalRow(totals, "Amount Paid", model.getAmountPaid(), normal);
        addTotalRow(totals, "Balance Due", model.getBalanceAfterPayment(), normal);
        document.add(totals);

        document.add(new Paragraph("Payment Mode: " + model.getPaymentMode(), normal));
        if (model.getTransactionRef() != null && !model.getTransactionRef().isBlank()) {
            document.add(new Paragraph("Transaction Ref: " + model.getTransactionRef(), normal));
        }
        document.add(new Paragraph("Collected By: " + model.getCollectedByName(), normal));

        if (receiptProperties.isQrEnabled() && model.getQrPayload() != null) {
            Image qr = buildQrImage(model.getQrPayload());
            if (qr != null) {
                qr.setAlignment(Element.ALIGN_RIGHT);
                document.add(qr);
            }
        }

        if (model.getFooterMessage() != null) {
            Paragraph footer = new Paragraph(model.getFooterMessage(), small);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(12f);
            document.add(footer);
        }

        PdfPTable signatures = new PdfPTable(2);
        signatures.setWidthPercentage(100);
        signatures.setSpacingBefore(24f);
        signatures.addCell(signatureCell(model.getSignatureLabel1(), normal));
        signatures.addCell(signatureCell(model.getSignatureLabel2(), normal));
        document.add(signatures);

        document.close();
        writer.close();
        return baos.toByteArray();
    }

    private static void addReprintWatermark(PdfWriter writer, Document document) {
        PdfContentByte canvas = writer.getDirectContentUnder();
        PdfGState state = new PdfGState();
        state.setFillOpacity(0.15f);
        canvas.saveState();
        canvas.setGState(state);
        canvas.beginText();
        try {
            BaseFont base = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            canvas.setFontAndSize(base, 72);
            canvas.setColorFill(Color.LIGHT_GRAY);
            canvas.showTextAligned(
                    Element.ALIGN_CENTER, "REPRINT", document.getPageSize().getWidth() / 2, 400, 45);
        } catch (Exception e) {
            log.warn("Could not render reprint watermark: {}", e.getMessage());
        }
        canvas.endText();
        canvas.restoreState();
    }

    private static Image buildQrImage(String payload) {
        try {
            BitMatrix matrix = new QRCodeWriter().encode(payload, BarcodeFormat.QR_CODE, 120, 120);
            java.awt.image.BufferedImage buffered = MatrixToImageWriter.toBufferedImage(matrix);
            return Image.getInstance(buffered, null);
        } catch (Exception e) {
            log.warn("QR generation failed: {}", e.getMessage());
            return null;
        }
    }

    private static void addMetaRow(PdfPTable table, String label, String value, Font font) {
        table.addCell(bodyCell(label, font));
        table.addCell(bodyCell(value != null ? value : "", font));
    }

    private static PdfPCell headerCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new Color(230, 230, 230));
        return cell;
    }

    private static PdfPCell bodyCell(String text, Font font) {
        return new PdfPCell(new Phrase(text != null ? text : "", font));
    }

    private static void addTotalRow(PdfPTable table, String label, BigDecimal amount, Font font) {
        table.addCell(bodyCell(label, font));
        table.addCell(bodyCell(formatMoney(amount), font));
    }

    private static PdfPCell signatureCell(String label, Font font) {
        String text = (label != null ? label : "Signature") + "\n\n_____________________";
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    private static String formatMoney(BigDecimal value) {
        return value != null ? value.toPlainString() : "0.00";
    }
}
