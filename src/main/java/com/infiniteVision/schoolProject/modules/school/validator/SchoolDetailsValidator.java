package com.infiniteVision.schoolProject.modules.school.validator;

import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.exception.ValidationException;
import com.infiniteVision.schoolProject.modules.school.dto.request.CreateSchoolDetailsRequestDTO;
import com.infiniteVision.schoolProject.modules.school.dto.request.UpdateSchoolDetailsRequestDTO;
import com.infiniteVision.schoolProject.modules.school.entity.SchoolDetails;
import com.infiniteVision.schoolProject.modules.school.repository.SchoolDetailsRepository;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Business validation for school create and update.
 */
@Component
@RequiredArgsConstructor
public class SchoolDetailsValidator {

    private final SchoolDetailsRepository schoolDetailsRepository;

    public void validateCreate(CreateSchoolDetailsRequestDTO request) {
        List<String> errors = new ArrayList<>();
        validateYearRange(request.getEstablishedYear(), errors);
        validateLateFeeConfiguration(request.getLateFeeApplicable(), request.getLateFeeAmount(), request.getLateFeeType(), errors);

        String schoolCode = normalizeCode(request.getSchoolCode());
        if (schoolDetailsRepository.existsBySchoolCodeAndDeletedFalse(schoolCode)) {
            errors.add(MessageConstants.SCHOOL_CODE_ALREADY_EXISTS);
        }

        String udiseCode = trimToNull(request.getUdiseCode());
        if (udiseCode != null && schoolDetailsRepository.existsByUdiseCodeAndDeletedFalse(udiseCode)) {
            errors.add(MessageConstants.SCHOOL_UDISE_ALREADY_EXISTS);
        }

        String affiliationNo = trimToNull(request.getAffiliationNo());
        if (affiliationNo != null && schoolDetailsRepository.existsByAffiliationNoAndDeletedFalse(affiliationNo)) {
            errors.add(MessageConstants.SCHOOL_AFFILIATION_ALREADY_EXISTS);
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(MessageConstants.VALIDATION_FAILED, errors);
        }
    }

    public void validateUpdate(Long schoolId, UpdateSchoolDetailsRequestDTO request, SchoolDetails existing) {
        List<String> errors = new ArrayList<>();
        if (!hasAnyField(request)) {
            errors.add(MessageConstants.SCHOOL_UPDATE_EMPTY);
        }

        Integer establishedYear = request.getEstablishedYear() != null ? request.getEstablishedYear() : existing.getEstablishedYear();
        validateYearRange(establishedYear, errors);

        Boolean lateFeeApplicable = request.getLateFeeApplicable() != null
                ? request.getLateFeeApplicable()
                : existing.getLateFeeApplicable();
        var lateFeeAmount = request.getLateFeeAmount() != null ? request.getLateFeeAmount() : existing.getLateFeeAmount();
        var lateFeeType = request.getLateFeeType() != null ? request.getLateFeeType() : existing.getLateFeeType();
        validateLateFeeConfiguration(lateFeeApplicable, lateFeeAmount, lateFeeType, errors);

        String schoolCode = request.getSchoolCode() != null ? normalizeCode(request.getSchoolCode()) : existing.getSchoolCode();
        if (schoolDetailsRepository.existsBySchoolCodeAndIdNotAndDeletedFalse(schoolCode, schoolId)) {
            errors.add(MessageConstants.SCHOOL_CODE_ALREADY_EXISTS);
        }

        String udiseCode = request.getUdiseCode() != null ? trimToNull(request.getUdiseCode()) : existing.getUdiseCode();
        if (udiseCode != null && schoolDetailsRepository.existsByUdiseCodeAndIdNotAndDeletedFalse(udiseCode, schoolId)) {
            errors.add(MessageConstants.SCHOOL_UDISE_ALREADY_EXISTS);
        }

        String affiliationNo =
                request.getAffiliationNo() != null ? trimToNull(request.getAffiliationNo()) : existing.getAffiliationNo();
        if (affiliationNo != null
                && schoolDetailsRepository.existsByAffiliationNoAndIdNotAndDeletedFalse(affiliationNo, schoolId)) {
            errors.add(MessageConstants.SCHOOL_AFFILIATION_ALREADY_EXISTS);
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(MessageConstants.VALIDATION_FAILED, errors);
        }
    }

    private static void validateLateFeeConfiguration(
            Boolean lateFeeApplicable, Object lateFeeAmount, Object lateFeeType, List<String> errors) {
        if (Boolean.TRUE.equals(lateFeeApplicable) && (lateFeeAmount == null || lateFeeType == null)) {
            errors.add(MessageConstants.SCHOOL_LATE_FEE_CONFIG_REQUIRED);
        }
    }

    private static void validateYearRange(Integer establishedYear, List<String> errors) {
        if (establishedYear == null) {
            return;
        }
        int currentYear = Year.now().getValue();
        if (establishedYear < 1800 || establishedYear > currentYear) {
            errors.add(MessageConstants.SCHOOL_ESTABLISHED_YEAR_INVALID);
        }
    }

    private static String normalizeCode(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }

    private static String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private static boolean hasAnyField(UpdateSchoolDetailsRequestDTO request) {
        return request.getSchoolName() != null
                || request.getSchoolCode() != null
                || request.getSchoolType() != null
                || request.getSchoolCategory() != null
                || request.getMediumOfInstruction() != null
                || request.getEstablishedYear() != null
                || request.getAffiliationNo() != null
                || request.getAffiliationBoard() != null
                || request.getUdiseCode() != null
                || request.getTrustName() != null
                || request.getTrustRegNo() != null
                || request.getPhonePrimary() != null
                || request.getPhoneSecondary() != null
                || request.getEmail() != null
                || request.getWebsite() != null
                || request.getFax() != null
                || request.getAddressLine1() != null
                || request.getAddressLine2() != null
                || request.getCity() != null
                || request.getDistrict() != null
                || request.getState() != null
                || request.getPincode() != null
                || request.getCountry() != null
                || request.getLatitude() != null
                || request.getLongitude() != null
                || request.getPrincipalName() != null
                || request.getPrincipalPhone() != null
                || request.getPrincipalEmail() != null
                || request.getLogoUrl() != null
                || request.getBannerUrl() != null
                || request.getSignatureUrl() != null
                || request.getSchoolMotto() != null
                || request.getSchoolColorPrimary() != null
                || request.getSchoolColorSecondary() != null
                || request.getAcademicYearStart() != null
                || request.getWorkingDaysPerWeek() != null
                || request.getTotalClasses() != null
                || request.getInvoicePrefix() != null
                || request.getReceiptPrefix() != null
                || request.getCurrency() != null
                || request.getLateFeeApplicable() != null
                || request.getLateFeeAmount() != null
                || request.getLateFeeType() != null
                || request.getGracePeriodDays() != null
                || request.getActive() != null;
    }
}
