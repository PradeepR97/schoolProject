package com.infiniteVision.schoolProject.modules.masterdata.service.impl;

import com.infiniteVision.schoolProject.modules.academic.entity.ClassMaster;
import com.infiniteVision.schoolProject.modules.academic.entity.SectionMaster;
import com.infiniteVision.schoolProject.modules.academic.repository.AcademicYearRepository;
import com.infiniteVision.schoolProject.modules.academic.repository.ClassMasterRepository;
import com.infiniteVision.schoolProject.modules.academic.repository.SectionMasterRepository;
import com.infiniteVision.schoolProject.modules.academic.util.ClassSectionDisplayFormatter;
import com.infiniteVision.schoolProject.modules.auth.entity.User;
import com.infiniteVision.schoolProject.modules.auth.repository.UserRepository;
import com.infiniteVision.schoolProject.modules.fees.entity.FeeHead;
import com.infiniteVision.schoolProject.modules.fees.repository.FeeHeadRepository;
import com.infiniteVision.schoolProject.modules.masterdata.constants.MasterDataStaticOptions;
import com.infiniteVision.schoolProject.modules.masterdata.dto.MasterDataOptionDTO;
import com.infiniteVision.schoolProject.modules.masterdata.service.MasterDataService;
import com.infiniteVision.schoolProject.modules.masterdata.util.MasterDataEnumFormatter;
import com.infiniteVision.schoolProject.modules.payment.enums.FeeBillingTerm;
import com.infiniteVision.schoolProject.modules.payment.enums.InvoiceStatus;
import com.infiniteVision.schoolProject.modules.payment.enums.LedgerStatus;
import com.infiniteVision.schoolProject.modules.payment.enums.PaymentMode;
import com.infiniteVision.schoolProject.modules.payment.enums.PaymentRecordStatus;
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
 * Aggregates dropdown options from enums and master tables for form UIs.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MasterDataServiceImpl implements MasterDataService {

    private final AcademicYearRepository academicYearRepository;
    private final SectionMasterRepository sectionMasterRepository;
    private final ClassMasterRepository classMasterRepository;
    private final FeeHeadRepository feeHeadRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<MasterDataOptionDTO>> getMasterData(Long academicYearId) {
        Map<String, List<MasterDataOptionDTO>> masterData = new LinkedHashMap<>();

        masterData.put("gender", MasterDataEnumFormatter.fromEnum(Gender.values()));
        masterData.put("medium", MasterDataEnumFormatter.fromEnum(Medium.values()));
        masterData.put("bloodGroup", MasterDataEnumFormatter.fromEnum(BloodGroup.values()));
        masterData.put("religion", MasterDataEnumFormatter.fromEnum(Religion.values()));
        masterData.put("community", MasterDataEnumFormatter.fromEnum(Community.values()));
        masterData.put("studentStatus", MasterDataEnumFormatter.fromEnum(StudentStatus.values()));
        masterData.put("feesPaymentStatus", MasterDataEnumFormatter.fromEnum(FeesPaymentStatus.values()));
        masterData.put("primaryContact", MasterDataEnumFormatter.fromEnum(PrimaryContact.values()));
        masterData.put(
                "documentVerificationStatus", MasterDataEnumFormatter.fromEnum(DocumentVerificationStatus.values()));
        masterData.put("feeBillingTerm", MasterDataEnumFormatter.fromEnum(FeeBillingTerm.values()));
        masterData.put("paymentMode", MasterDataEnumFormatter.fromEnum(PaymentMode.values()));
        masterData.put("ledgerStatus", MasterDataEnumFormatter.fromEnum(LedgerStatus.values()));
        masterData.put("invoiceStatus", MasterDataEnumFormatter.fromEnum(InvoiceStatus.values()));
        masterData.put("paymentRecordStatus", MasterDataEnumFormatter.fromEnum(PaymentRecordStatus.values()));

        masterData.put("feeCategory", MasterDataStaticOptions.FEE_CATEGORIES);

        masterData.put("academicYear", mapAcademicYears());
        masterData.put("section", mapSections());
        masterData.put("class", mapClasses(academicYearId));
        masterData.put("feeHead", mapFeeHeads());
        masterData.put("user", mapUsers());

        log.debug("Master data built with {} keys, academicYearId={}", masterData.size(), academicYearId);
        return masterData;
    }

    private List<MasterDataOptionDTO> mapAcademicYears() {
        return academicYearRepository.findAllByDeletedFalseOrderByStartDateDesc().stream()
                .map(year -> MasterDataOptionDTO.builder()
                        .id(year.getId())
                        .label(year.getYearName())
                        .value(String.valueOf(year.getId()))
                        .build())
                .toList();
    }

    private List<MasterDataOptionDTO> mapSections() {
        return sectionMasterRepository.findAllByActiveTrueAndDeletedFalseOrderByDisplayOrderAscSectionCodeAsc().stream()
                .map(this::toSectionOption)
                .toList();
    }

    private MasterDataOptionDTO toSectionOption(SectionMaster section) {
        String label = section.getSectionName() != null && !section.getSectionName().isBlank()
                ? section.getSectionName()
                : "Section " + section.getSectionCode();
        return MasterDataOptionDTO.builder()
                .id(section.getId())
                .label(label)
                .value(String.valueOf(section.getId()))
                .build();
    }

    private List<MasterDataOptionDTO> mapClasses(Long academicYearId) {
        List<ClassMaster> classes;
        if (academicYearId != null) {
            classes = classMasterRepository.findAllWithAcademicYearByAcademicYearIdAndDeletedFalseOrderByClassNameAsc(
                    academicYearId);
        } else {
            classes = classMasterRepository.findAllWithAcademicYearByDeletedFalseOrderByClassNameAsc();
        }
        return classes.stream().map(this::toClassOption).toList();
    }

    private MasterDataOptionDTO toClassOption(ClassMaster classMaster) {
        String label = ClassSectionDisplayFormatter.formatGradeOnly(classMaster.getClassName());
        return MasterDataOptionDTO.builder()
                .id(classMaster.getId())
                .label(label)
                .value(String.valueOf(classMaster.getId()))
                .build();
    }

    private List<MasterDataOptionDTO> mapFeeHeads() {
        return feeHeadRepository.findAllByActiveTrueAndDeletedFalseOrderByDisplayOrderAscFeeHeadNameAsc().stream()
                .map(this::toFeeHeadOption)
                .toList();
    }

    private MasterDataOptionDTO toFeeHeadOption(FeeHead feeHead) {
        return MasterDataOptionDTO.builder()
                .id(feeHead.getId())
                .label(feeHead.getFeeHeadName())
                .value(String.valueOf(feeHead.getId()))
                .build();
    }

    private List<MasterDataOptionDTO> mapUsers() {
        return userRepository.findAllByDeletedFalseOrderByIdAsc().stream()
                .map(this::toUserOption)
                .toList();
    }

    private MasterDataOptionDTO toUserOption(User user) {
        return MasterDataOptionDTO.builder()
                .id(user.getId())
                .label(user.getFullName())
                .value(String.valueOf(user.getId()))
                .build();
    }
}
