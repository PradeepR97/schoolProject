package com.infiniteVision.schoolProject.modules.excelupload.parser;

import com.infiniteVision.schoolProject.modules.student.dto.request.StudentAdmissionRequestDTO;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 * Result of parsing one Excel data row before persistence.
 */
@Getter
public class ParsedStudentAdmissionRow {

    private final int rowNumber;
    private final StudentAdmissionRequestDTO request;
    private final List<String> errors = new ArrayList<>();

    public ParsedStudentAdmissionRow(int rowNumber) {
        this.rowNumber = rowNumber;
        this.request = null;
    }

    public ParsedStudentAdmissionRow(int rowNumber, StudentAdmissionRequestDTO request) {
        this.rowNumber = rowNumber;
        this.request = request;
    }

    public void addError(String error) {
        errors.add(error);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public boolean isSkippableEmptyRow() {
        return request == null && errors.isEmpty();
    }
}
