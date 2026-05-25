package com.infiniteVision.schoolProject.modules.excelupload.parser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.NumberToTextConverter;

/**
 * Reads cell values from Apache POI rows as normalized strings and typed values.
 */
public final class ExcelCellValueReader {

    private static final DataFormatter FORMATTER = new DataFormatter();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    private ExcelCellValueReader() {
    }

    public static String readString(Row row, Integer columnIndex) {
        if (columnIndex == null) {
            return null;
        }
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            return null;
        }
        String value = extractPlainText(cell, row.getSheet().getWorkbook());
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    /**
     * Reads a fixed-length numeric identifier (e.g. 12-digit Aadhar) from Excel numeric or text cells.
     */
    public static String readFixedDigitString(
            Row row, Integer columnIndex, int exactLength, String fieldLabel, List<String> errors) {
        String raw = readString(row, columnIndex);
        if (raw == null) {
            return null;
        }
        String normalized = normalizeDigitString(raw);
        if (normalized.length() != exactLength) {
            errors.add(fieldLabel + " must be exactly " + exactLength + " digits");
            return null;
        }
        if (!normalized.matches("\\d{" + exactLength + "}")) {
            errors.add(fieldLabel + " must contain only digits");
            return null;
        }
        return normalized;
    }

    private static String extractPlainText(Cell cell, org.apache.poi.ss.usermodel.Workbook workbook) {
        CellType cellType = cell.getCellType();
        if (cellType == CellType.FORMULA) {
            cellType = cell.getCachedFormulaResultType();
        }
        return switch (cellType) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield FORMATTER.formatCellValue(cell);
                }
                yield NumberToTextConverter.toText(cell.getNumericCellValue());
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case BLANK -> null;
            default -> {
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                yield FORMATTER.formatCellValue(cell, evaluator);
            }
        };
    }

    /**
     * Converts scientific notation or decimal tails from Excel number cells to plain digits.
     */
    private static String normalizeDigitString(String raw) {
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            return trimmed;
        }
        if (trimmed.contains("E") || trimmed.contains("e")) {
            return new BigDecimal(trimmed).toBigInteger().toString();
        }
        int decimalIndex = trimmed.indexOf('.');
        if (decimalIndex >= 0) {
            String fraction = trimmed.substring(decimalIndex + 1);
            if (fraction.replace("0", "").isEmpty()) {
                return trimmed.substring(0, decimalIndex);
            }
        }
        return trimmed.replaceAll("\\D", "");
    }

    public static Long readLong(Row row, Integer columnIndex, String fieldLabel, List<String> errors) {
        String raw = readString(row, columnIndex);
        if (raw == null) {
            return null;
        }
        try {
            if (raw.contains(".")) {
                return new BigDecimal(raw).longValue();
            }
            return Long.parseLong(raw);
        } catch (NumberFormatException ex) {
            errors.add(fieldLabel + " must be a valid number");
            return null;
        }
    }

    public static BigDecimal readBigDecimal(Row row, Integer columnIndex, String fieldLabel, List<String> errors) {
        String raw = readString(row, columnIndex);
        if (raw == null) {
            return null;
        }
        try {
            return new BigDecimal(raw);
        } catch (NumberFormatException ex) {
            errors.add(fieldLabel + " must be a valid amount");
            return null;
        }
    }

    public static LocalDate readLocalDate(Row row, Integer columnIndex, String fieldLabel, List<String> errors) {
        if (columnIndex == null) {
            return null;
        }
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            Date date = cell.getDateCellValue();
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        String raw = readString(row, columnIndex);
        if (raw == null) {
            return null;
        }
        try {
            return LocalDate.parse(raw, DATE_FORMAT);
        } catch (DateTimeParseException ex) {
            errors.add(fieldLabel + " must be in yyyy-MM-dd format");
            return null;
        }
    }
}
