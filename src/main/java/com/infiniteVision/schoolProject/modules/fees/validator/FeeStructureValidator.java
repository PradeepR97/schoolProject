package com.infiniteVision.schoolProject.modules.fees.validator;

import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.exception.ValidationException;
import com.infiniteVision.schoolProject.modules.academic.entity.ClassMaster;
import com.infiniteVision.schoolProject.modules.academic.repository.AcademicYearRepository;
import com.infiniteVision.schoolProject.modules.academic.repository.ClassMasterRepository;
import com.infiniteVision.schoolProject.modules.fees.dto.request.CreateFeeStructureRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.request.UpdateFeeStructureRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.entity.FeeStructure;
import com.infiniteVision.schoolProject.modules.fees.enums.TermType;
import com.infiniteVision.schoolProject.modules.fees.repository.FeeHeadRepository;
import com.infiniteVision.schoolProject.modules.fees.repository.FeeStructureRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Business validation for fee structure create and update.
 */
@Component
@RequiredArgsConstructor
public class FeeStructureValidator {

    private final AcademicYearRepository academicYearRepository;
    private final ClassMasterRepository classMasterRepository;
    private final FeeHeadRepository feeHeadRepository;
    private final FeeStructureRepository feeStructureRepository;

    /**
     * Validates create request including uniqueness of year + class + head + term.
     */
    public void validateCreate(CreateFeeStructureRequestDTO request) {
        List<String> errors = new ArrayList<>();
        validateReferences(
                request.getAcademicYearId(),
                request.getClassId(),
                request.getFeeHeadId(),
                errors);
        if (feeStructureRepository.existsByAcademicYear_IdAndClassMaster_IdAndFeeHead_IdAndTermTypeAndDeletedFalse(
                request.getAcademicYearId(),
                request.getClassId(),
                request.getFeeHeadId(),
                request.getTermType())) {
            errors.add(MessageConstants.FEE_STRUCTURE_ALREADY_EXISTS);
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(MessageConstants.VALIDATION_FAILED, errors);
        }
    }

    /**
     * Validates partial update; merges ids with existing row for uniqueness check.
     */
    public void validateUpdate(Long structureId, UpdateFeeStructureRequestDTO request, FeeStructure existing) {
        List<String> errors = new ArrayList<>();

        if (!hasAnyField(request)) {
            errors.add(MessageConstants.FEE_STRUCTURE_UPDATE_EMPTY);
        }

        Long academicYearId =
                request.getAcademicYearId() != null ? request.getAcademicYearId() : existing.getAcademicYear().getId();
        Long classId = request.getClassId() != null ? request.getClassId() : existing.getClassMaster().getId();
        Long feeHeadId = request.getFeeHeadId() != null ? request.getFeeHeadId() : existing.getFeeHead().getId();
        TermType termType = request.getTermType() != null ? request.getTermType() : existing.getTermType();

        validateReferences(academicYearId, classId, feeHeadId, errors);

        if (feeStructureRepository
                .existsByAcademicYear_IdAndClassMaster_IdAndFeeHead_IdAndTermTypeAndIdNotAndDeletedFalse(
                        academicYearId, classId, feeHeadId, termType, structureId)) {
            errors.add(MessageConstants.FEE_STRUCTURE_ALREADY_EXISTS);
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(MessageConstants.VALIDATION_FAILED, errors);
        }
    }

    private void validateReferences(
            Long academicYearId, Long classId, Long feeHeadId, List<String> errors) {
        if (!academicYearRepository.findByIdAndDeletedFalse(academicYearId).isPresent()) {
            errors.add(MessageConstants.ACADEMIC_YEAR_NOT_FOUND);
            return;
        }

        ClassMaster classMaster = classMasterRepository.findByIdAndDeletedFalse(classId).orElse(null);
        if (classMaster == null) {
            errors.add(MessageConstants.CLASS_NOT_FOUND);
            return;
        }

        if (!academicYearId.equals(classMaster.getAcademicYear().getId())) {
            errors.add(MessageConstants.CLASS_ACADEMIC_YEAR_MISMATCH);
        }

        if (!feeHeadRepository.findByIdAndDeletedFalse(feeHeadId).isPresent()) {
            errors.add(MessageConstants.FEE_HEAD_NOT_FOUND);
        }
    }

    private static boolean hasAnyField(UpdateFeeStructureRequestDTO request) {
        return request.getAcademicYearId() != null
                || request.getClassId() != null
                || request.getFeeHeadId() != null
                || request.getTermType() != null
                || request.getAmount() != null
                || request.getDueDate() != null
                || request.getLateFeeDaily() != null
                || request.getMaxLateFee() != null
                || request.getScholarshipAllowed() != null
                || request.getInstallmentAllowed() != null
                || request.getActive() != null
                || request.getRemarks() != null;
    }
}
