package com.infiniteVision.schoolProject.modules.excelupload.parser;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Expected header names for the student admission Excel template (row 1).
 */
@Getter
@RequiredArgsConstructor
public enum StudentAdmissionExcelColumn {

    ADMISSION_NO("admissionNo", true),
    FIRST_NAME("firstName", true),
    LAST_NAME("lastName", false),
    DATE_OF_BIRTH("dateOfBirth", false),
    MEDIUM("medium", true),
    GENDER("gender", false),
    CLASS_ID("classId", true),
    ACADEMIC_YEAR_ID("academicYearId", true),
    AADHAR_NUMBER("aadharNumber", false),
    EMIS_NUMBER("emisNumber", false),
    RATION_CARD_NUMBER("rationCardNumber", false),
    NATIONALITY("nationality", false),
    ADDRESS("address", false),
    BLOOD_GROUP("bloodGroup", false),
    RELIGION("religion", false),
    COMMUNITY("community", false),
    ANNUAL_INCOME("annualIncome", false),
    STATUS("status", false),
    FEES_PAYMENT_STATUS("feesPaymentStatus", false),
    FATHER_NAME("fatherName", false),
    FATHER_PHONE("fatherPhone", false),
    FATHER_EMAIL("fatherEmail", false),
    FATHER_OCCUPATION("fatherOccupation", false),
    FATHER_ANNUAL_INCOME("fatherAnnualIncome", false),
    MOTHER_NAME("motherName", false),
    MOTHER_PHONE("motherPhone", false),
    MOTHER_EMAIL("motherEmail", false),
    MOTHER_OCCUPATION("motherOccupation", false),
    MOTHER_ANNUAL_INCOME("motherAnnualIncome", false),
    GUARDIAN_NAME("guardianName", false),
    GUARDIAN_PHONE("guardianPhone", false),
    GUARDIAN_EMAIL("guardianEmail", false),
    GUARDIAN_OCCUPATION("guardianOccupation", false),
    GUARDIAN_RELATIONSHIP("guardianRelationship", false),
    PRIMARY_CONTACT("primaryContact", true),
    PROFILE_PHOTO_URL("profilePhotoUrl", false),
    AADHAR_NO("aadharNo", false);

    private final String header;
    private final boolean requiredInTemplate;

    public static List<String> templateHeaders() {
        return Arrays.stream(values())
                .map(StudentAdmissionExcelColumn::getHeader)
                .collect(Collectors.toList());
    }

    public static List<StudentAdmissionExcelColumn> requiredColumns() {
        return Arrays.stream(values())
                .filter(StudentAdmissionExcelColumn::isRequiredInTemplate)
                .collect(Collectors.toList());
    }
}
