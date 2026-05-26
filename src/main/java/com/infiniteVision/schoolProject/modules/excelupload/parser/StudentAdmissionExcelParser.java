package com.infiniteVision.schoolProject.modules.excelupload.parser;

import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.exception.ValidationException;
import com.infiniteVision.schoolProject.modules.excelupload.config.ExcelUploadProperties;
import com.infiniteVision.schoolProject.modules.student.dto.request.StudentAdmissionDocumentsDTO;
import com.infiniteVision.schoolProject.modules.student.dto.request.StudentAdmissionParentsDTO;
import com.infiniteVision.schoolProject.modules.student.dto.request.StudentAdmissionRequestDTO;
import com.infiniteVision.schoolProject.modules.student.dto.request.StudentAdmissionStudentDTO;
import com.infiniteVision.schoolProject.modules.student.enums.BloodGroup;
import com.infiniteVision.schoolProject.modules.student.enums.Community;
import com.infiniteVision.schoolProject.modules.student.enums.FeesPaymentStatus;
import com.infiniteVision.schoolProject.modules.student.enums.Gender;
import com.infiniteVision.schoolProject.modules.student.enums.Medium;
import com.infiniteVision.schoolProject.modules.student.enums.PrimaryContact;
import com.infiniteVision.schoolProject.modules.student.enums.Religion;
import com.infiniteVision.schoolProject.modules.student.enums.StudentStatus;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Parses student admission rows from an uploaded .xlsx file.
 */
@Component
@RequiredArgsConstructor
public class StudentAdmissionExcelParser {

    private final ExcelUploadProperties excelUploadProperties;

    /**
     * Reads the first sheet and maps each data row to {@link StudentAdmissionRequestDTO}.
     */
    public List<ParsedStudentAdmissionRow> parse(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream();
                Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getNumberOfSheets() > 0 ? workbook.getSheetAt(0) : null;
            if (sheet == null || sheet.getPhysicalNumberOfRows() == 0) {
                throw new ValidationException(MessageConstants.EXCEL_UPLOAD_NO_DATA_ROWS);
            }
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new ValidationException(MessageConstants.EXCEL_UPLOAD_MISSING_HEADER_ROW);
            }
            Map<StudentAdmissionExcelColumn, Integer> columnIndexByHeader = mapHeaders(headerRow);
            List<ParsedStudentAdmissionRow> rows = new ArrayList<>();
            int lastRow = sheet.getLastRowNum();
            for (int rowIndex = 1; rowIndex <= lastRow; rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue;
                }
                ParsedStudentAdmissionRow parsed = parseDataRow(rowIndex + 1, row, columnIndexByHeader);
                if (!parsed.isSkippableEmptyRow()) {
                    rows.add(parsed);
                }
            }
            if (rows.isEmpty()) {
                throw new ValidationException(MessageConstants.EXCEL_UPLOAD_NO_DATA_ROWS);
            }
            if (rows.size() > excelUploadProperties.getMaxRows()) {
                throw new ValidationException(MessageConstants.EXCEL_UPLOAD_MAX_ROWS_EXCEEDED);
            }
            return rows;
        } catch (IOException ex) {
            throw new ValidationException(MessageConstants.INVALID_REQUEST, List.of(ex.getMessage()));
        }
    }

    private Map<StudentAdmissionExcelColumn, Integer> mapHeaders(Row headerRow) {
        Map<String, StudentAdmissionExcelColumn> headerLookup = new HashMap<>();
        for (StudentAdmissionExcelColumn column : StudentAdmissionExcelColumn.values()) {
            headerLookup.put(normalizeHeader(column.getHeader()), column);
        }
        Map<StudentAdmissionExcelColumn, Integer> indexMap = new EnumMap<>(StudentAdmissionExcelColumn.class);
        short lastCell = headerRow.getLastCellNum();
        for (int cellIndex = 0; cellIndex < lastCell; cellIndex++) {
            String header = ExcelCellValueReader.readString(headerRow, cellIndex);
            if (header == null) {
                continue;
            }
            StudentAdmissionExcelColumn column = headerLookup.get(normalizeHeader(header));
            if (column != null) {
                indexMap.put(column, cellIndex);
            }
        }
        List<String> missing = new ArrayList<>();
        for (StudentAdmissionExcelColumn required : StudentAdmissionExcelColumn.requiredColumns()) {
            if (!indexMap.containsKey(required)) {
                missing.add(MessageConstants.EXCEL_UPLOAD_MISSING_REQUIRED_COLUMN + required.getHeader());
            }
        }
        if (!missing.isEmpty()) {
            throw new ValidationException(MessageConstants.VALIDATION_FAILED, missing);
        }
        return indexMap;
    }

    private ParsedStudentAdmissionRow parseDataRow(
            int rowNumber, Row row, Map<StudentAdmissionExcelColumn, Integer> columns) {
        List<String> errors = new ArrayList<>();
        String admissionNo = ExcelCellValueReader.readString(row, columns.get(StudentAdmissionExcelColumn.ADMISSION_NO));
        String firstName = ExcelCellValueReader.readString(row, columns.get(StudentAdmissionExcelColumn.FIRST_NAME));
        if (isBlank(admissionNo) && isBlank(firstName)) {
            return new ParsedStudentAdmissionRow(rowNumber);
        }

        if (isBlank(admissionNo)) {
            errors.add("admissionNo is required");
        }
        if (isBlank(firstName)) {
            errors.add("firstName is required");
        }

        Medium medium = parseEnum(
                ExcelCellValueReader.readString(row, columns.get(StudentAdmissionExcelColumn.MEDIUM)),
                Medium.class,
                "medium",
                errors);
        Long classId = ExcelCellValueReader.readLong(
                row, columns.get(StudentAdmissionExcelColumn.CLASS_ID), "classId", errors);
        Long sectionId = ExcelCellValueReader.readLong(
                row, columns.get(StudentAdmissionExcelColumn.SECTION_ID), "sectionId", errors);
        Long academicYearId = ExcelCellValueReader.readLong(
                row, columns.get(StudentAdmissionExcelColumn.ACADEMIC_YEAR_ID), "academicYearId", errors);
        if (classId == null) {
            errors.add("classId is required");
        }
        if (sectionId == null) {
            errors.add("sectionId is required");
        }
        if (academicYearId == null) {
            errors.add("academicYearId is required");
        }
        if (medium == null) {
            errors.add("medium is required");
        }

        PrimaryContact primaryContact = parseEnum(
                ExcelCellValueReader.readString(row, columns.get(StudentAdmissionExcelColumn.PRIMARY_CONTACT)),
                PrimaryContact.class,
                "primaryContact",
                errors);
        if (primaryContact == null) {
            errors.add("primaryContact is required");
        }

        StudentAdmissionStudentDTO student = StudentAdmissionStudentDTO.builder()
                .admissionNo(admissionNo)
                .firstName(firstName)
                .lastName(ExcelCellValueReader.readString(row, columns.get(StudentAdmissionExcelColumn.LAST_NAME)))
                .dateOfBirth(ExcelCellValueReader.readLocalDate(
                        row, columns.get(StudentAdmissionExcelColumn.DATE_OF_BIRTH), "dateOfBirth", errors))
                .medium(medium)
                .gender(parseEnum(
                        ExcelCellValueReader.readString(row, columns.get(StudentAdmissionExcelColumn.GENDER)),
                        Gender.class,
                        "gender",
                        errors))
                .classId(classId)
                .sectionId(sectionId)
                .academicYearId(academicYearId)
                .aadharNumber(ExcelCellValueReader.readFixedDigitString(
                        row,
                        columns.get(StudentAdmissionExcelColumn.AADHAR_NUMBER),
                        12,
                        "aadharNumber",
                        errors))
                .emisNumber(ExcelCellValueReader.readString(
                        row, columns.get(StudentAdmissionExcelColumn.EMIS_NUMBER)))
                .rationCardNumber(ExcelCellValueReader.readString(
                        row, columns.get(StudentAdmissionExcelColumn.RATION_CARD_NUMBER)))
                .nationality(ExcelCellValueReader.readString(
                        row, columns.get(StudentAdmissionExcelColumn.NATIONALITY)))
                .address(ExcelCellValueReader.readString(row, columns.get(StudentAdmissionExcelColumn.ADDRESS)))
                .bloodGroup(parseEnum(
                        ExcelCellValueReader.readString(row, columns.get(StudentAdmissionExcelColumn.BLOOD_GROUP)),
                        BloodGroup.class,
                        "bloodGroup",
                        errors))
                .religion(parseEnum(
                        ExcelCellValueReader.readString(row, columns.get(StudentAdmissionExcelColumn.RELIGION)),
                        Religion.class,
                        "religion",
                        errors))
                .community(parseEnum(
                        ExcelCellValueReader.readString(row, columns.get(StudentAdmissionExcelColumn.COMMUNITY)),
                        Community.class,
                        "community",
                        errors))
                .annualIncome(ExcelCellValueReader.readBigDecimal(
                        row, columns.get(StudentAdmissionExcelColumn.ANNUAL_INCOME), "annualIncome", errors))
                .status(parseEnum(
                        ExcelCellValueReader.readString(row, columns.get(StudentAdmissionExcelColumn.STATUS)),
                        StudentStatus.class,
                        "status",
                        errors))
                .feesPaymentStatus(parseEnum(
                        ExcelCellValueReader.readString(
                                row, columns.get(StudentAdmissionExcelColumn.FEES_PAYMENT_STATUS)),
                        FeesPaymentStatus.class,
                        "feesPaymentStatus",
                        errors))
                .build();

        StudentAdmissionParentsDTO parents = StudentAdmissionParentsDTO.builder()
                .fatherName(ExcelCellValueReader.readString(
                        row, columns.get(StudentAdmissionExcelColumn.FATHER_NAME)))
                .fatherPhone(ExcelCellValueReader.readString(
                        row, columns.get(StudentAdmissionExcelColumn.FATHER_PHONE)))
                .fatherEmail(ExcelCellValueReader.readString(
                        row, columns.get(StudentAdmissionExcelColumn.FATHER_EMAIL)))
                .fatherOccupation(ExcelCellValueReader.readString(
                        row, columns.get(StudentAdmissionExcelColumn.FATHER_OCCUPATION)))
                .fatherAnnualIncome(ExcelCellValueReader.readBigDecimal(
                        row,
                        columns.get(StudentAdmissionExcelColumn.FATHER_ANNUAL_INCOME),
                        "fatherAnnualIncome",
                        errors))
                .motherName(ExcelCellValueReader.readString(
                        row, columns.get(StudentAdmissionExcelColumn.MOTHER_NAME)))
                .motherPhone(ExcelCellValueReader.readString(
                        row, columns.get(StudentAdmissionExcelColumn.MOTHER_PHONE)))
                .motherEmail(ExcelCellValueReader.readString(
                        row, columns.get(StudentAdmissionExcelColumn.MOTHER_EMAIL)))
                .motherOccupation(ExcelCellValueReader.readString(
                        row, columns.get(StudentAdmissionExcelColumn.MOTHER_OCCUPATION)))
                .motherAnnualIncome(ExcelCellValueReader.readBigDecimal(
                        row,
                        columns.get(StudentAdmissionExcelColumn.MOTHER_ANNUAL_INCOME),
                        "motherAnnualIncome",
                        errors))
                .guardianName(ExcelCellValueReader.readString(
                        row, columns.get(StudentAdmissionExcelColumn.GUARDIAN_NAME)))
                .guardianPhone(ExcelCellValueReader.readString(
                        row, columns.get(StudentAdmissionExcelColumn.GUARDIAN_PHONE)))
                .guardianEmail(ExcelCellValueReader.readString(
                        row, columns.get(StudentAdmissionExcelColumn.GUARDIAN_EMAIL)))
                .guardianOccupation(ExcelCellValueReader.readString(
                        row, columns.get(StudentAdmissionExcelColumn.GUARDIAN_OCCUPATION)))
                .guardianRelationship(ExcelCellValueReader.readString(
                        row, columns.get(StudentAdmissionExcelColumn.GUARDIAN_RELATIONSHIP)))
                .primaryContact(primaryContact)
                .build();

        String profilePhotoUrl = ExcelCellValueReader.readString(
                row, columns.get(StudentAdmissionExcelColumn.PROFILE_PHOTO_URL));
        String aadharNo = ExcelCellValueReader.readFixedDigitString(
                row, columns.get(StudentAdmissionExcelColumn.AADHAR_NO), 12, "aadharNo", errors);
        StudentAdmissionDocumentsDTO documents = null;
        if (profilePhotoUrl != null || aadharNo != null) {
            documents = StudentAdmissionDocumentsDTO.builder()
                    .profilePhotoUrl(profilePhotoUrl)
                    .aadharNo(aadharNo)
                    .build();
        }

        if (!errors.isEmpty()) {
            ParsedStudentAdmissionRow failed = new ParsedStudentAdmissionRow(rowNumber);
            failed.getErrors().addAll(errors);
            return failed;
        }

        StudentAdmissionRequestDTO request = StudentAdmissionRequestDTO.builder()
                .student(student)
                .parents(parents)
                .documents(documents)
                .build();
        return new ParsedStudentAdmissionRow(rowNumber, request);
    }

    private static <E extends Enum<E>> E parseEnum(String raw, Class<E> enumType, String fieldLabel, List<String> errors) {
        if (isBlank(raw)) {
            return null;
        }
        try {
            return Enum.valueOf(enumType, raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            errors.add("Invalid " + fieldLabel + ": " + raw);
            return null;
        }
    }

    private static String normalizeHeader(String header) {
        return header.trim().toLowerCase(Locale.ROOT).replace(" ", "");
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
