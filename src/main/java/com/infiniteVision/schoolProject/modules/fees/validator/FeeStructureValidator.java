package com.infiniteVision.schoolProject.modules.fees.validator;

import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.exception.ValidationException;
import com.infiniteVision.schoolProject.modules.academic.validator.AcademicEnrollmentValidator;
import com.infiniteVision.schoolProject.modules.fees.dto.request.CreateFeeStructureRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.request.UpdateFeeStructureRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.entity.FeeStructure;
import com.infiniteVision.schoolProject.modules.fees.enums.TermType;
import com.infiniteVision.schoolProject.modules.fees.repository.FeeStructureRepository;
import com.infiniteVision.schoolProject.modules.fees.repository.FeeTypeRepository;
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

    private final AcademicEnrollmentValidator academicEnrollmentValidator;
    private final FeeTypeRepository feeTypeRepository;
    private final FeeStructureRepository feeStructureRepository;

    /**
     * Validates create request including uniqueness of year + class + fee type + term.
     */
    public void validateCreate(CreateFeeStructureRequestDTO request) {
        List<String> errors = new ArrayList<>();
        validateReferences(request.getAcademicYearId(), request.getClassId(), request.getFeeTypeId(), errors);
        if (feeStructureRepository.existsByAcademicYear_IdAndClassMaster_IdAndFeeType_IdAndTermTypeAndDeletedFalse(
                request.getAcademicYearId(), request.getClassId(), request.getFeeTypeId(), request.getTermType())) {
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
        Long feeTypeId = request.getFeeTypeId() != null ? request.getFeeTypeId() : existing.getFeeType().getId();
        TermType termType = request.getTermType() != null ? request.getTermType() : existing.getTermType();

        validateReferences(academicYearId, classId, feeTypeId, errors);

        if (feeStructureRepository.existsByAcademicYear_IdAndClassMaster_IdAndFeeType_IdAndTermTypeAndIdNotAndDeletedFalse(
                academicYearId, classId, feeTypeId, termType, structureId)) {
            errors.add(MessageConstants.FEE_STRUCTURE_ALREADY_EXISTS);
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(MessageConstants.VALIDATION_FAILED, errors);
        }
    }

    private void validateReferences(Long academicYearId, Long classId, Long feeTypeId, List<String> errors) {
        academicEnrollmentValidator.validateClassForAcademicYear(academicYearId, classId, errors);

        if (!feeTypeRepository.findByIdAndDeletedFalse(feeTypeId).isPresent()) {
            errors.add(MessageConstants.FEE_TYPE_NOT_FOUND);
        }
    }

    private static boolean hasAnyField(UpdateFeeStructureRequestDTO request) {
        return request.getAcademicYearId() != null
                || request.getClassId() != null
                || request.getFeeTypeId() != null
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
