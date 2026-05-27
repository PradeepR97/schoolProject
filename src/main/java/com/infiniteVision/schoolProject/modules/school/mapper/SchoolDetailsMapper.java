package com.infiniteVision.schoolProject.modules.school.mapper;

import com.infiniteVision.schoolProject.modules.school.dto.request.CreateSchoolDetailsRequestDTO;
import com.infiniteVision.schoolProject.modules.school.dto.request.UpdateSchoolDetailsRequestDTO;
import com.infiniteVision.schoolProject.modules.school.dto.response.SchoolDetailsResponseDTO;
import com.infiniteVision.schoolProject.modules.school.entity.SchoolDetails;
import com.infiniteVision.schoolProject.modules.school.enums.MediumOfInstruction;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Maps school entity and DTOs, including medium-of-instruction CSV conversion.
 */
@Component
public class SchoolDetailsMapper {

    public SchoolDetails toEntity(CreateSchoolDetailsRequestDTO request) {
        return SchoolDetails.builder()
                .schoolName(trim(request.getSchoolName()))
                .schoolCode(normalizeCode(request.getSchoolCode()))
                .schoolType(request.getSchoolType())
                .schoolCategory(request.getSchoolCategory())
                .mediumOfInstruction(toMediumCsv(request.getMediumOfInstruction()))
                .establishedYear(request.getEstablishedYear())
                .affiliationNo(trimToNull(request.getAffiliationNo()))
                .affiliationBoard(request.getAffiliationBoard())
                .udiseCode(trimToNull(request.getUdiseCode()))
                .trustName(trimToNull(request.getTrustName()))
                .trustRegNo(trimToNull(request.getTrustRegNo()))
                .phonePrimary(trim(request.getPhonePrimary()))
                .phoneSecondary(trimToNull(request.getPhoneSecondary()))
                .email(normalizeEmail(request.getEmail()))
                .website(trimToNull(request.getWebsite()))
                .fax(trimToNull(request.getFax()))
                .addressLine1(trim(request.getAddressLine1()))
                .addressLine2(trimToNull(request.getAddressLine2()))
                .city(trim(request.getCity()))
                .district(trim(request.getDistrict()))
                .state(defaultIfBlank(request.getState(), "Tamil Nadu"))
                .pincode(trim(request.getPincode()))
                .country(defaultIfBlank(request.getCountry(), "India"))
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .principalName(trimToNull(request.getPrincipalName()))
                .principalPhone(trimToNull(request.getPrincipalPhone()))
                .principalEmail(normalizeEmailNullable(request.getPrincipalEmail()))
                .logoUrl(trimToNull(request.getLogoUrl()))
                .bannerUrl(trimToNull(request.getBannerUrl()))
                .signatureUrl(trimToNull(request.getSignatureUrl()))
                .schoolMotto(trimToNull(request.getSchoolMotto()))
                .schoolColorPrimary(trimToNull(request.getSchoolColorPrimary()))
                .schoolColorSecondary(trimToNull(request.getSchoolColorSecondary()))
                .academicYearStart(
                        request.getAcademicYearStart() != null
                                ? request.getAcademicYearStart()
                                : com.infiniteVision.schoolProject.modules.school.enums.AcademicYearStartMonth.JUNE)
                .workingDaysPerWeek(request.getWorkingDaysPerWeek() != null ? request.getWorkingDaysPerWeek() : 6)
                .totalClasses(request.getTotalClasses())
                .invoicePrefix(defaultIfBlank(request.getInvoicePrefix(), "INV"))
                .receiptPrefix(defaultIfBlank(request.getReceiptPrefix(), "RCP"))
                .currency(defaultIfBlank(request.getCurrency(), "INR"))
                .lateFeeApplicable(request.getLateFeeApplicable() != null ? request.getLateFeeApplicable() : Boolean.FALSE)
                .lateFeeAmount(request.getLateFeeAmount())
                .lateFeeType(request.getLateFeeType())
                .gracePeriodDays(request.getGracePeriodDays() != null ? request.getGracePeriodDays() : 0)
                .active(request.getActive() != null ? request.getActive() : Boolean.TRUE)
                .deleted(Boolean.FALSE)
                .build();
    }

    public void applyUpdates(SchoolDetails entity, UpdateSchoolDetailsRequestDTO request) {
        if (request.getSchoolName() != null) entity.setSchoolName(trim(request.getSchoolName()));
        if (request.getSchoolCode() != null) entity.setSchoolCode(normalizeCode(request.getSchoolCode()));
        if (request.getSchoolType() != null) entity.setSchoolType(request.getSchoolType());
        if (request.getSchoolCategory() != null) entity.setSchoolCategory(request.getSchoolCategory());
        if (request.getMediumOfInstruction() != null) entity.setMediumOfInstruction(toMediumCsv(request.getMediumOfInstruction()));
        if (request.getEstablishedYear() != null) entity.setEstablishedYear(request.getEstablishedYear());
        if (request.getAffiliationNo() != null) entity.setAffiliationNo(trimToNull(request.getAffiliationNo()));
        if (request.getAffiliationBoard() != null) entity.setAffiliationBoard(request.getAffiliationBoard());
        if (request.getUdiseCode() != null) entity.setUdiseCode(trimToNull(request.getUdiseCode()));
        if (request.getTrustName() != null) entity.setTrustName(trimToNull(request.getTrustName()));
        if (request.getTrustRegNo() != null) entity.setTrustRegNo(trimToNull(request.getTrustRegNo()));
        if (request.getPhonePrimary() != null) entity.setPhonePrimary(trim(request.getPhonePrimary()));
        if (request.getPhoneSecondary() != null) entity.setPhoneSecondary(trimToNull(request.getPhoneSecondary()));
        if (request.getEmail() != null) entity.setEmail(normalizeEmail(request.getEmail()));
        if (request.getWebsite() != null) entity.setWebsite(trimToNull(request.getWebsite()));
        if (request.getFax() != null) entity.setFax(trimToNull(request.getFax()));
        if (request.getAddressLine1() != null) entity.setAddressLine1(trim(request.getAddressLine1()));
        if (request.getAddressLine2() != null) entity.setAddressLine2(trimToNull(request.getAddressLine2()));
        if (request.getCity() != null) entity.setCity(trim(request.getCity()));
        if (request.getDistrict() != null) entity.setDistrict(trim(request.getDistrict()));
        if (request.getState() != null) entity.setState(trim(request.getState()));
        if (request.getPincode() != null) entity.setPincode(trim(request.getPincode()));
        if (request.getCountry() != null) entity.setCountry(trim(request.getCountry()));
        if (request.getLatitude() != null) entity.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) entity.setLongitude(request.getLongitude());
        if (request.getPrincipalName() != null) entity.setPrincipalName(trimToNull(request.getPrincipalName()));
        if (request.getPrincipalPhone() != null) entity.setPrincipalPhone(trimToNull(request.getPrincipalPhone()));
        if (request.getPrincipalEmail() != null) entity.setPrincipalEmail(normalizeEmailNullable(request.getPrincipalEmail()));
        if (request.getLogoUrl() != null) entity.setLogoUrl(trimToNull(request.getLogoUrl()));
        if (request.getBannerUrl() != null) entity.setBannerUrl(trimToNull(request.getBannerUrl()));
        if (request.getSignatureUrl() != null) entity.setSignatureUrl(trimToNull(request.getSignatureUrl()));
        if (request.getSchoolMotto() != null) entity.setSchoolMotto(trimToNull(request.getSchoolMotto()));
        if (request.getSchoolColorPrimary() != null) entity.setSchoolColorPrimary(trimToNull(request.getSchoolColorPrimary()));
        if (request.getSchoolColorSecondary() != null) entity.setSchoolColorSecondary(trimToNull(request.getSchoolColorSecondary()));
        if (request.getAcademicYearStart() != null) entity.setAcademicYearStart(request.getAcademicYearStart());
        if (request.getWorkingDaysPerWeek() != null) entity.setWorkingDaysPerWeek(request.getWorkingDaysPerWeek());
        if (request.getTotalClasses() != null) entity.setTotalClasses(request.getTotalClasses());
        if (request.getInvoicePrefix() != null) entity.setInvoicePrefix(trim(request.getInvoicePrefix()));
        if (request.getReceiptPrefix() != null) entity.setReceiptPrefix(trim(request.getReceiptPrefix()));
        if (request.getCurrency() != null) entity.setCurrency(trim(request.getCurrency()));
        if (request.getLateFeeApplicable() != null) entity.setLateFeeApplicable(request.getLateFeeApplicable());
        if (request.getLateFeeAmount() != null) entity.setLateFeeAmount(request.getLateFeeAmount());
        if (request.getLateFeeType() != null) entity.setLateFeeType(request.getLateFeeType());
        if (request.getGracePeriodDays() != null) entity.setGracePeriodDays(request.getGracePeriodDays());
        if (request.getActive() != null) entity.setActive(request.getActive());
    }

    public SchoolDetailsResponseDTO toResponse(SchoolDetails entity) {
        return SchoolDetailsResponseDTO.builder()
                .schoolId(entity.getId())
                .schoolName(entity.getSchoolName())
                .schoolCode(entity.getSchoolCode())
                .schoolType(entity.getSchoolType())
                .schoolCategory(entity.getSchoolCategory())
                .mediumOfInstruction(toMediumList(entity.getMediumOfInstruction()))
                .establishedYear(entity.getEstablishedYear())
                .affiliationNo(entity.getAffiliationNo())
                .affiliationBoard(entity.getAffiliationBoard())
                .udiseCode(entity.getUdiseCode())
                .trustName(entity.getTrustName())
                .trustRegNo(entity.getTrustRegNo())
                .phonePrimary(entity.getPhonePrimary())
                .phoneSecondary(entity.getPhoneSecondary())
                .email(entity.getEmail())
                .website(entity.getWebsite())
                .fax(entity.getFax())
                .addressLine1(entity.getAddressLine1())
                .addressLine2(entity.getAddressLine2())
                .city(entity.getCity())
                .district(entity.getDistrict())
                .state(entity.getState())
                .pincode(entity.getPincode())
                .country(entity.getCountry())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .principalName(entity.getPrincipalName())
                .principalPhone(entity.getPrincipalPhone())
                .principalEmail(entity.getPrincipalEmail())
                .logoUrl(entity.getLogoUrl())
                .bannerUrl(entity.getBannerUrl())
                .signatureUrl(entity.getSignatureUrl())
                .schoolMotto(entity.getSchoolMotto())
                .schoolColorPrimary(entity.getSchoolColorPrimary())
                .schoolColorSecondary(entity.getSchoolColorSecondary())
                .academicYearStart(entity.getAcademicYearStart())
                .workingDaysPerWeek(entity.getWorkingDaysPerWeek())
                .totalClasses(entity.getTotalClasses())
                .invoicePrefix(entity.getInvoicePrefix())
                .receiptPrefix(entity.getReceiptPrefix())
                .currency(entity.getCurrency())
                .lateFeeApplicable(entity.getLateFeeApplicable())
                .lateFeeAmount(entity.getLateFeeAmount())
                .lateFeeType(entity.getLateFeeType())
                .gracePeriodDays(entity.getGracePeriodDays())
                .active(entity.getActive())
                .build();
    }

    private static String toMediumCsv(List<MediumOfInstruction> values) {
        Set<MediumOfInstruction> ordered = new LinkedHashSet<>(values);
        return ordered.stream().map(Enum::name).collect(Collectors.joining(","));
    }

    private static List<MediumOfInstruction> toMediumList(String csv) {
        if (csv == null || csv.isBlank()) {
            return List.of();
        }
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(MediumOfInstruction::valueOf)
                .toList();
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }

    private static String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private static String defaultIfBlank(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }

    private static String normalizeCode(String value) {
        return value == null ? null : value.trim().toUpperCase(Locale.ROOT);
    }

    private static String normalizeEmail(String value) {
        return value == null ? null : value.trim().toLowerCase(Locale.ROOT);
    }

    private static String normalizeEmailNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
