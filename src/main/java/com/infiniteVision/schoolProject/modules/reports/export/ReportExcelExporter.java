package com.infiniteVision.schoolProject.modules.reports.export;

import com.infiniteVision.schoolProject.modules.reports.model.ReportDataset;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.io.IOException;
import java.io.UncheckedIOException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

/**
 * Exports {@link ReportDataset} to .xlsx bytes.
 */
@Component
public class ReportExcelExporter {

    public byte[] export(ReportDataset dataset) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(sanitizeSheetName(dataset.getSheetName()));
            int rowIndex = 0;
            Row titleRow = sheet.createRow(rowIndex++);
            titleRow.createCell(0).setCellValue(dataset.getReportTitle());
            Row headerRow = sheet.createRow(rowIndex++);
            for (int columnIndex = 0; columnIndex < dataset.getHeaders().size(); columnIndex++) {
                headerRow.createCell(columnIndex).setCellValue(dataset.getHeaders().get(columnIndex));
            }
            int columnCount = dataset.getHeaders().size();
            for (List<String> dataRowValues : dataset.getRows()) {
                Row dataRow = sheet.createRow(rowIndex++);
                for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
                    String cellValue =
                            columnIndex < dataRowValues.size() ? dataRowValues.get(columnIndex) : "";
                    dataRow.createCell(columnIndex).setCellValue(cellValue);
                }
            }
            for (int columnIndex = 0; columnIndex < dataset.getHeaders().size(); columnIndex++) {
                sheet.autoSizeColumn(columnIndex);
            }
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new UncheckedIOException("Failed to export report to Excel", ex);
        }
    }

    private static String sanitizeSheetName(String name) {
        String sanitized = name.replaceAll("[\\\\/*?\\[\\]:]", "_");
        return sanitized.length() > 31 ? sanitized.substring(0, 31) : sanitized;
    }
}
