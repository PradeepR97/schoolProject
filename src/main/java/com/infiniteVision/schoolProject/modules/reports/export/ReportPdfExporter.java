package com.infiniteVision.schoolProject.modules.reports.export;

import com.infiniteVision.schoolProject.modules.reports.model.ReportDataset;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Exports {@link ReportDataset} to PDF bytes using OpenPDF.
 */
@Component
public class ReportPdfExporter {

    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
    private static final Font BODY_FONT = FontFactory.getFont(FontFactory.HELVETICA, 8);

    public byte[] export(ReportDataset dataset) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate(), 36, 36, 54, 36);
            PdfWriter.getInstance(document, outputStream);
            document.open();
            Paragraph title = new Paragraph(dataset.getReportTitle(), TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(12f);
            document.add(title);
            PdfPTable table = buildTable(dataset);
            document.add(table);
            document.close();
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new UncheckedIOException("Failed to export report to PDF", ex);
        } catch (DocumentException ex) {
            throw new IllegalStateException("Failed to export report to PDF", ex);
        }
    }

    private static PdfPTable buildTable(ReportDataset dataset) throws DocumentException {
        int columnCount = dataset.getHeaders().size();
        PdfPTable table = new PdfPTable(columnCount);
        table.setWidthPercentage(100f);
        for (String header : dataset.getHeaders()) {
            table.addCell(headerCell(header));
        }
        for (List<String> row : dataset.getRows()) {
            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                String value = columnIndex < row.size() ? row.get(columnIndex) : "";
                table.addCell(bodyCell(value));
            }
        }
        return table;
    }

    private static PdfPCell headerCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", HEADER_FONT));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(4f);
        return cell;
    }

    private static PdfPCell bodyCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", BODY_FONT));
        cell.setPadding(3f);
        return cell;
    }
}
