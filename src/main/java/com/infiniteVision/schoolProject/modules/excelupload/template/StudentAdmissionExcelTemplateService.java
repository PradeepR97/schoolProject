package com.infiniteVision.schoolProject.modules.excelupload.template;

import com.infiniteVision.schoolProject.modules.excelupload.parser.StudentAdmissionExcelColumn;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

/**
 * Builds downloadable .xlsx templates for student admission bulk upload.
 */
@Service
public class StudentAdmissionExcelTemplateService {

    public static final String TEMPLATE_FILENAME = "student-admission-template.xlsx";
    public static final String SAMPLE_FILENAME = "student-admission-sample-2-rows.xlsx";

    /**
     * @return workbook bytes with header row for all supported columns
     */
    public byte[] buildTemplateBytes() {
        return buildWorkbookBytes(false);
    }

    /**
     * @return workbook bytes with header row plus two sample student data rows
     */
    public byte[] buildSampleBytes() {
        return buildWorkbookBytes(true);
    }

    private byte[] buildWorkbookBytes(boolean includeSampleRows) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Admissions");
            XSSFCellStyle textStyle = createTextCellStyle(workbook);
            Row headerRow = sheet.createRow(0);
            int columnIndex = 0;
            for (String header : StudentAdmissionExcelColumn.templateHeaders()) {
                headerRow.createCell(columnIndex++).setCellValue(header);
            }
            if (includeSampleRows) {
                int rowIndex = 1;
                for (Object[] sampleRow : StudentAdmissionExcelSampleData.SAMPLE_ROWS) {
                    Row dataRow = sheet.createRow(rowIndex++);
                    writeRow(dataRow, sampleRow, textStyle);
                }
            }
            for (int index = 0; index < StudentAdmissionExcelColumn.values().length; index++) {
                sheet.autoSizeColumn(index);
            }
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new UncheckedIOException("Failed to build student admission Excel workbook", ex);
        }
    }

    private static XSSFCellStyle createTextCellStyle(XSSFWorkbook workbook) {
        DataFormat dataFormat = workbook.createDataFormat();
        XSSFCellStyle textStyle = workbook.createCellStyle();
        textStyle.setDataFormat(dataFormat.getFormat("@"));
        return textStyle;
    }

    private static void writeRow(Row row, Object[] values, XSSFCellStyle textStyle) {
        for (int columnIndex = 0; columnIndex < values.length; columnIndex++) {
            Cell cell = row.createCell(columnIndex);
            Object value = values[columnIndex];
            if (value == null) {
                continue;
            }
            boolean forceText = isTextColumn(columnIndex);
            if (forceText) {
                cell.setCellStyle(textStyle);
                cell.setCellValue(String.valueOf(value));
            } else if (value instanceof Number number) {
                cell.setCellValue(number.doubleValue());
            } else {
                cell.setCellValue(String.valueOf(value));
            }
        }
    }

    private static boolean isTextColumn(int columnIndex) {
        StudentAdmissionExcelColumn[] columns = StudentAdmissionExcelColumn.values();
        if (columnIndex >= columns.length) {
            return false;
        }
        StudentAdmissionExcelColumn column = columns[columnIndex];
        return StudentAdmissionExcelColumn.AADHAR_NUMBER.equals(column)
                || StudentAdmissionExcelColumn.AADHAR_NO.equals(column)
                || StudentAdmissionExcelColumn.ADMISSION_NO.equals(column)
                || StudentAdmissionExcelColumn.EMIS_NUMBER.equals(column)
                || StudentAdmissionExcelColumn.RATION_CARD_NUMBER.equals(column);
    }
}
