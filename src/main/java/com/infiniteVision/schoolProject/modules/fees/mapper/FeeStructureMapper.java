package com.infiniteVision.schoolProject.modules.fees.mapper;

import com.infiniteVision.schoolProject.modules.academic.entity.AcademicYear;
import com.infiniteVision.schoolProject.modules.academic.entity.ClassMaster;
import com.infiniteVision.schoolProject.modules.academic.repository.AcademicYearRepository;
import com.infiniteVision.schoolProject.modules.academic.repository.ClassMasterRepository;
import com.infiniteVision.schoolProject.modules.fees.dto.request.CreateFeeStructureRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.request.UpdateFeeStructureRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.response.FeeStructureResponseDTO;
import com.infiniteVision.schoolProject.modules.fees.entity.FeeHead;
import com.infiniteVision.schoolProject.modules.fees.entity.FeeStructure;
import com.infiniteVision.schoolProject.modules.fees.repository.FeeHeadRepository;
import com.infiniteVision.schoolProject.modules.student.mapper.StudentListMapper;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Maps {@link FeeStructure} entities to DTOs and applies partial updates.
 */
@Component
@RequiredArgsConstructor
public class FeeStructureMapper {

    private final AcademicYearRepository academicYearRepository;
    private final ClassMasterRepository classMasterRepository;
    private final FeeHeadRepository feeHeadRepository;
    private final StudentListMapper studentListMapper;

    public FeeStructure toEntity(CreateFeeStructureRequestDTO request) {
        AcademicYear academicYear = academicYearRepository
                .findByIdAndDeletedFalse(request.getAcademicYearId())
                .orElseThrow();
        ClassMaster classMaster = classMasterRepository
                .findByIdAndDeletedFalse(request.getClassId())
                .orElseThrow();
        FeeHead feeHead = feeHeadRepository.findByIdAndDeletedFalse(request.getFeeHeadId()).orElseThrow();

        return FeeStructure.builder()
                .academicYear(academicYear)
                .classMaster(classMaster)
                .feeHead(feeHead)
                .termType(request.getTermType())
                .amount(request.getAmount())
                .dueDate(request.getDueDate())
                .lateFeeDaily(defaultDecimal(request.getLateFeeDaily(), BigDecimal.ZERO))
                .maxLateFee(defaultDecimal(request.getMaxLateFee(), BigDecimal.ZERO))
                .scholarshipAllowed(
                        request.getScholarshipAllowed() != null ? request.getScholarshipAllowed() : Boolean.TRUE)
                .installmentAllowed(
                        request.getInstallmentAllowed() != null ? request.getInstallmentAllowed() : Boolean.FALSE)
                .active(request.getActive() != null ? request.getActive() : Boolean.TRUE)
                .remarks(trimToNull(request.getRemarks()))
                .deleted(Boolean.FALSE)
                .build();
    }

    public void applyUpdates(FeeStructure entity, UpdateFeeStructureRequestDTO request) {
        if (request.getAcademicYearId() != null) {
            entity.setAcademicYear(academicYearRepository
                    .findByIdAndDeletedFalse(request.getAcademicYearId())
                    .orElseThrow());
        }
        if (request.getClassId() != null) {
            entity.setClassMaster(classMasterRepository
                    .findByIdAndDeletedFalse(request.getClassId())
                    .orElseThrow());
        }
        if (request.getFeeHeadId() != null) {
            entity.setFeeHead(feeHeadRepository.findByIdAndDeletedFalse(request.getFeeHeadId()).orElseThrow());
        }
        if (request.getTermType() != null) {
            entity.setTermType(request.getTermType());
        }
        if (request.getAmount() != null) {
            entity.setAmount(request.getAmount());
        }
        if (request.getDueDate() != null) {
            entity.setDueDate(request.getDueDate());
        }
        if (request.getLateFeeDaily() != null) {
            entity.setLateFeeDaily(request.getLateFeeDaily());
        }
        if (request.getMaxLateFee() != null) {
            entity.setMaxLateFee(request.getMaxLateFee());
        }
        if (request.getScholarshipAllowed() != null) {
            entity.setScholarshipAllowed(request.getScholarshipAllowed());
        }
        if (request.getInstallmentAllowed() != null) {
            entity.setInstallmentAllowed(request.getInstallmentAllowed());
        }
        if (request.getActive() != null) {
            entity.setActive(request.getActive());
        }
        if (request.getRemarks() != null) {
            entity.setRemarks(trimToNull(request.getRemarks()));
        }
    }

    public FeeStructureResponseDTO toResponse(FeeStructure entity) {
        return FeeStructureResponseDTO.builder()
                .structureId(entity.getId())
                .academicYearId(entity.getAcademicYear().getId())
                .academicYearName(entity.getAcademicYear().getYearName())
                .classId(entity.getClassMaster().getId())
                .className(studentListMapper.formatClassName(entity.getClassMaster()))
                .feeHeadId(entity.getFeeHead().getId())
                .feeHeadCode(entity.getFeeHead().getFeeHeadCode())
                .feeHeadName(entity.getFeeHead().getFeeHeadName())
                .termType(entity.getTermType())
                .amount(entity.getAmount())
                .dueDate(entity.getDueDate())
                .lateFeeDaily(entity.getLateFeeDaily())
                .maxLateFee(entity.getMaxLateFee())
                .scholarshipAllowed(entity.getScholarshipAllowed())
                .installmentAllowed(entity.getInstallmentAllowed())
                .active(entity.getActive())
                .remarks(entity.getRemarks())
                .build();
    }

    private static BigDecimal defaultDecimal(BigDecimal value, BigDecimal defaultValue) {
        return value != null ? value : defaultValue;
    }

    private static String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
