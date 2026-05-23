package com.infiniteVision.schoolProject.modules.lookup.service.impl;

import com.infiniteVision.schoolProject.modules.academic.entity.ClassMaster;
import com.infiniteVision.schoolProject.modules.academic.entity.SectionMaster;
import com.infiniteVision.schoolProject.modules.academic.repository.AcademicYearRepository;
import com.infiniteVision.schoolProject.modules.academic.repository.ClassMasterRepository;
import com.infiniteVision.schoolProject.modules.academic.repository.SectionMasterRepository;
import com.infiniteVision.schoolProject.modules.auth.entity.User;
import com.infiniteVision.schoolProject.modules.auth.enums.UserRole;
import com.infiniteVision.schoolProject.modules.auth.enums.UserStatus;
import com.infiniteVision.schoolProject.modules.auth.repository.UserRepository;
import com.infiniteVision.schoolProject.modules.fees.entity.FeeHead;
import com.infiniteVision.schoolProject.modules.fees.enums.TermType;
import com.infiniteVision.schoolProject.modules.fees.repository.FeeHeadRepository;
import com.infiniteVision.schoolProject.modules.lookup.constants.LookupStaticOptions;
import com.infiniteVision.schoolProject.modules.lookup.dto.LookupOptionDTO;
import com.infiniteVision.schoolProject.modules.lookup.service.LookupService;
import com.infiniteVision.schoolProject.modules.lookup.util.EnumLookupFormatter;
import com.infiniteVision.schoolProject.modules.payment.enums.FeeBillingTerm;
import com.infiniteVision.schoolProject.modules.payment.enums.InvoiceStatus;
import com.infiniteVision.schoolProject.modules.payment.enums.LedgerStatus;
import com.infiniteVision.schoolProject.modules.payment.enums.PaymentMode;
import com.infiniteVision.schoolProject.modules.payment.enums.PaymentRecordStatus;
import com.infiniteVision.schoolProject.modules.scholarship.enums.ApplicableTo;
import com.infiniteVision.schoolProject.modules.scholarship.enums.DiscountType;
import com.infiniteVision.schoolProject.modules.scholarship.enums.SchemeType;
import com.infiniteVision.schoolProject.modules.student.enums.BloodGroup;
import com.infiniteVision.schoolProject.modules.student.enums.Community;
import com.infiniteVision.schoolProject.modules.student.enums.DocumentVerificationStatus;
import com.infiniteVision.schoolProject.modules.student.enums.FeesPaymentStatus;
import com.infiniteVision.schoolProject.modules.student.enums.Gender;
import com.infiniteVision.schoolProject.modules.student.enums.Medium;
import com.infiniteVision.schoolProject.modules.student.enums.PrimaryContact;
import com.infiniteVision.schoolProject.modules.student.enums.Religion;
import com.infiniteVision.schoolProject.modules.student.enums.StudentStatus;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Builds a single JSON map of dropdown options from enums and master tables.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LookupServiceImpl implements LookupService {

    private final AcademicYearRepository academicYearRepository;
    private final SectionMasterRepository sectionMasterRepository;
    private final ClassMasterRepository classMasterRepository;
    private final FeeHeadRepository feeHeadRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<LookupOptionDTO>> getAllLookups(Long academicYearId) {
        Map<String, List<LookupOptionDTO>> lookups = new LinkedHashMap<>();

        lookups.put("role", EnumLookupFormatter.fromEnum(UserRole.values()));
        lookups.put("userStatus", EnumLookupFormatter.fromEnum(UserStatus.values()));
        lookups.put("gender", EnumLookupFormatter.fromEnum(Gender.values()));
        lookups.put("bloodGroup", EnumLookupFormatter.fromEnum(BloodGroup.values()));
        lookups.put("religion", EnumLookupFormatter.fromEnum(Religion.values()));
        lookups.put("community", EnumLookupFormatter.fromEnum(Community.values()));
        lookups.put("medium", EnumLookupFormatter.fromEnum(Medium.values()));
        lookups.put("studentStatus", EnumLookupFormatter.fromEnum(StudentStatus.values()));
        lookups.put("feesPaymentStatus", EnumLookupFormatter.fromEnum(FeesPaymentStatus.values()));
        lookups.put("primaryContact", EnumLookupFormatter.fromEnum(PrimaryContact.values()));
        lookups.put("documentVerificationStatus", EnumLookupFormatter.fromEnum(DocumentVerificationStatus.values()));
        lookups.put("schemeType", EnumLookupFormatter.fromEnum(SchemeType.values()));
        lookups.put("discountType", EnumLookupFormatter.fromEnum(DiscountType.values()));
        lookups.put("applicableTo", EnumLookupFormatter.fromEnum(ApplicableTo.values()));
        lookups.put("termType", EnumLookupFormatter.fromEnum(TermType.values()));
        lookups.put("feeBillingTerm", EnumLookupFormatter.fromEnum(FeeBillingTerm.values()));
        lookups.put("paymentMode", EnumLookupFormatter.fromEnum(PaymentMode.values()));
        lookups.put("ledgerStatus", EnumLookupFormatter.fromEnum(LedgerStatus.values()));
        lookups.put("invoiceStatus", EnumLookupFormatter.fromEnum(InvoiceStatus.values()));
        lookups.put("paymentRecordStatus", EnumLookupFormatter.fromEnum(PaymentRecordStatus.values()));

        lookups.put("feeCategory", LookupStaticOptions.FEE_CATEGORIES);

        lookups.put("academicYear", mapAcademicYears());
        lookups.put("section", mapSections());
        lookups.put("class", mapClasses(academicYearId));
        lookups.put("feeHead", mapFeeHeads());
        lookups.put("user", mapUsers());

        log.debug("Lookups built with {} keys, academicYearId={}", lookups.size(), academicYearId);
        return lookups;
    }

    private List<LookupOptionDTO> mapAcademicYears() {
        return academicYearRepository.findAllByDeletedFalseOrderByStartDateDesc().stream()
                .map(year -> LookupOptionDTO.builder()
                        .id(year.getId())
                        .label(year.getYearName())
                        .value(String.valueOf(year.getId()))
                        .build())
                .toList();
    }

    private List<LookupOptionDTO> mapSections() {
        return sectionMasterRepository.findAllByActiveTrueAndDeletedFalseOrderByDisplayOrderAscSectionCodeAsc().stream()
                .map(this::toSectionOption)
                .toList();
    }

    private LookupOptionDTO toSectionOption(SectionMaster section) {
        String label = section.getSectionName() != null && !section.getSectionName().isBlank()
                ? section.getSectionName()
                : "Section " + section.getSectionCode();
        return LookupOptionDTO.builder()
                .id(section.getId())
                .label(label)
                .value(String.valueOf(section.getId()))
                .build();
    }

    private List<LookupOptionDTO> mapClasses(Long academicYearId) {
        List<ClassMaster> classes;
        if (academicYearId != null) {
            classes = classMasterRepository.findAllWithSectionByAcademicYearIdAndDeletedFalseOrderByClassNameAsc(
                    academicYearId);
        } else {
            classes = classMasterRepository.findAllWithSectionByDeletedFalseOrderByClassNameAsc();
        }
        return classes.stream().map(this::toClassOption).toList();
    }

    private LookupOptionDTO toClassOption(ClassMaster classMaster) {
        String sectionCode = classMaster.getSection().getSectionCode();
        String className = classMaster.getClassName();
        String label = isPrePrimary(className)
                ? className + " - " + sectionCode
                : "Class " + className + " - " + sectionCode;
        return LookupOptionDTO.builder()
                .id(classMaster.getId())
                .label(label)
                .value(String.valueOf(classMaster.getId()))
                .build();
    }

    private boolean isPrePrimary(String className) {
        return "LKG".equalsIgnoreCase(className) || "UKG".equalsIgnoreCase(className);
    }

    private List<LookupOptionDTO> mapFeeHeads() {
        return feeHeadRepository.findAllByActiveTrueAndDeletedFalseOrderByDisplayOrderAscFeeHeadNameAsc().stream()
                .map(this::toFeeHeadOption)
                .toList();
    }

    private LookupOptionDTO toFeeHeadOption(FeeHead feeHead) {
        return LookupOptionDTO.builder()
                .id(feeHead.getId())
                .label(feeHead.getFeeHeadName())
                .value(String.valueOf(feeHead.getId()))
                .build();
    }

    private List<LookupOptionDTO> mapUsers() {
        return userRepository.findAllByDeletedFalseOrderByIdAsc().stream()
                .map(this::toUserOption)
                .toList();
    }

    private LookupOptionDTO toUserOption(User user) {
        return LookupOptionDTO.builder()
                .id(user.getId())
                .label(user.getFullName())
                .value(String.valueOf(user.getId()))
                .build();
    }
}
