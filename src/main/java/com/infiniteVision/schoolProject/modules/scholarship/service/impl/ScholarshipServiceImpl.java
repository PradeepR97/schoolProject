package com.infiniteVision.schoolProject.modules.scholarship.service.impl;

import com.infiniteVision.schoolProject.common.audit.enums.AuditEntityType;
import com.infiniteVision.schoolProject.common.audit.service.AuditService;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.exception.ResourceNotFoundException;
import com.infiniteVision.schoolProject.exception.UnauthorizedException;
import com.infiniteVision.schoolProject.exception.ValidationException;
import com.infiniteVision.schoolProject.security.AuthenticatedUser;
import com.infiniteVision.schoolProject.modules.academic.entity.AcademicYear;
import com.infiniteVision.schoolProject.modules.academic.repository.AcademicYearRepository;
import com.infiniteVision.schoolProject.modules.fees.entity.FeeType;
import com.infiniteVision.schoolProject.modules.fees.repository.FeeTypeRepository;
import com.infiniteVision.schoolProject.modules.scholarship.dto.request.CreateScholarshipRequestDTO;
import com.infiniteVision.schoolProject.modules.scholarship.dto.request.UpdateScholarshipRequestDTO;
import com.infiniteVision.schoolProject.modules.scholarship.dto.response.ScholarshipResponseDTO;
import com.infiniteVision.schoolProject.modules.scholarship.entity.SchoolScheme;
import com.infiniteVision.schoolProject.modules.scholarship.enums.ApplicableTo;
import com.infiniteVision.schoolProject.modules.scholarship.enums.DiscountType;
import com.infiniteVision.schoolProject.modules.scholarship.mapper.ScholarshipMapper;
import com.infiniteVision.schoolProject.modules.scholarship.repository.SchoolSchemeRepository;
import com.infiniteVision.schoolProject.modules.scholarship.service.ScholarshipService;
import com.infiniteVision.schoolProject.modules.scholarship.validator.ScholarshipValidator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of {@link ScholarshipService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScholarshipServiceImpl implements ScholarshipService {

    private final SchoolSchemeRepository schoolSchemeRepository;
    private final AcademicYearRepository academicYearRepository;
    private final FeeTypeRepository feeTypeRepository;
    private final ScholarshipMapper scholarshipMapper;
    private final ScholarshipValidator scholarshipValidator;
    private final AuditService auditService;

    @Override
    @Transactional
    public ScholarshipResponseDTO createScholarship(CreateScholarshipRequestDTO request) {
        AcademicYear academicYear = findAcademicYearOrThrow(request.getAcademicYearId());
        FeeType feeType = resolveFeeType(request.getApplicableTo(), request.getFeeTypeId());

        scholarshipValidator.validateFeeTypeForApplicableTo(request.getApplicableTo(), request.getFeeTypeId(), feeType);
        scholarshipValidator.validateDiscountValue(request.getDiscountType(), request.getDiscountValue());

        boolean active = request.getIsActive() != null ? request.getIsActive() : Boolean.TRUE;

        SchoolScheme scheme = SchoolScheme.builder()
                .schemeName(request.getSchemeName().trim())
                .schemeType(request.getSchemeType())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .applicableTo(request.getApplicableTo())
                .feeType(feeType)
                .academicYear(academicYear)
                .isActive(active)
                .deleted(Boolean.FALSE)
                .build();

        SchoolScheme saved = schoolSchemeRepository.save(scheme);
        ScholarshipResponseDTO afterSnapshot = scholarshipMapper.toResponse(saved);
        auditService.logCreate(AuditEntityType.SCHOLARSHIP, saved.getId(), afterSnapshot);
        log.info("Scholarship created id={} name={}", saved.getId(), saved.getSchemeName());
        return afterSnapshot;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScholarshipResponseDTO> listActiveScholarships() {
        List<ScholarshipResponseDTO> scholarships = schoolSchemeRepository
                .findByIsActiveTrueAndDeletedFalseOrderBySchemeNameAsc()
                .stream()
                .map(scholarshipMapper::toResponse)
                .toList();
        log.info("Active scholarships listed count={}", scholarships.size());
        return scholarships;
    }

    @Override
    @Transactional(readOnly = true)
    public ScholarshipResponseDTO getScholarshipById(Long schemeId) {
        SchoolScheme scheme = findActiveSchemeOrThrow(schemeId);
        log.info("Scholarship retrieved id={}", schemeId);
        return scholarshipMapper.toResponse(scheme);
    }

    @Override
    @Transactional
    public ScholarshipResponseDTO updateScholarship(Long schemeId, UpdateScholarshipRequestDTO request) {
        validateUpdateRequestHasFields(request);

        SchoolScheme scheme = findSchemeOrThrow(schemeId);
        ScholarshipResponseDTO beforeSnapshot = scholarshipMapper.toResponse(scheme);

        ApplicableTo applicableTo = request.getApplicableTo() != null ? request.getApplicableTo() : scheme.getApplicableTo();
        DiscountType discountType = request.getDiscountType() != null ? request.getDiscountType() : scheme.getDiscountType();

        if (request.getSchemeName() != null && !request.getSchemeName().isBlank()) {
            scheme.setSchemeName(request.getSchemeName().trim());
        }
        if (request.getSchemeType() != null) {
            scheme.setSchemeType(request.getSchemeType());
        }
        if (request.getDiscountType() != null) {
            scheme.setDiscountType(request.getDiscountType());
        }
        if (request.getDiscountValue() != null) {
            scheme.setDiscountValue(request.getDiscountValue());
        }
        if (request.getApplicableTo() != null) {
            scheme.setApplicableTo(request.getApplicableTo());
        }
        if (request.getAcademicYearId() != null) {
            scheme.setAcademicYear(findAcademicYearOrThrow(request.getAcademicYearId()));
        }
        if (request.getIsActive() != null) {
            scheme.setIsActive(request.getIsActive());
        }

        Long feeTypeId = request.getFeeTypeId();
        if (feeTypeId != null || request.getApplicableTo() != null) {
            if (ApplicableTo.SPECIFIC_HEAD.equals(applicableTo)) {
                Long resolvedFeeTypeId = feeTypeId != null ? feeTypeId : scheme.getFeeType() != null ? scheme.getFeeType().getId() : null;
                FeeType feeType = resolveFeeType(applicableTo, resolvedFeeTypeId);
                scholarshipValidator.validateFeeTypeForApplicableTo(applicableTo, resolvedFeeTypeId, feeType);
                scheme.setFeeType(feeType);
            } else {
                scheme.setFeeType(null);
            }
        }

        scholarshipValidator.validateDiscountValue(discountType, scheme.getDiscountValue());

        SchoolScheme saved = schoolSchemeRepository.save(scheme);
        ScholarshipResponseDTO afterSnapshot = scholarshipMapper.toResponse(saved);
        auditService.logUpdate(AuditEntityType.SCHOLARSHIP, schemeId, beforeSnapshot, afterSnapshot);
        log.info("Scholarship updated id={}", schemeId);
        return afterSnapshot;
    }

    /**
     * Deactivates a scholarship: {@code is_active = false} plus audit soft-delete
     * ({@code deleted}, {@code deleted_at}, {@code deleted_by}).
     */
    @Override
    @Transactional
    public void deactivateScholarship(Long schemeId) {
        SchoolScheme scheme = findSchemeOrThrow(schemeId);
        ScholarshipResponseDTO beforeSnapshot = scholarshipMapper.toResponse(scheme);
        if (Boolean.FALSE.equals(scheme.getIsActive()) && Boolean.TRUE.equals(scheme.getDeleted())) {
            log.info("Scholarship already deactivated id={}", schemeId);
            return;
        }

        Long deletedByUserId = currentUser().getUserId();
        scheme.setIsActive(Boolean.FALSE);
        scheme.markDeleted(deletedByUserId);
        schoolSchemeRepository.save(scheme);
        auditService.logDelete(AuditEntityType.SCHOLARSHIP, schemeId, beforeSnapshot);
        log.info("Scholarship deactivated id={} by user id={}", schemeId, deletedByUserId);
    }

    private void validateUpdateRequestHasFields(UpdateScholarshipRequestDTO request) {
        boolean hasField = (request.getSchemeName() != null && !request.getSchemeName().isBlank())
                || request.getSchemeType() != null
                || request.getDiscountType() != null
                || request.getDiscountValue() != null
                || request.getApplicableTo() != null
                || request.getFeeTypeId() != null
                || request.getAcademicYearId() != null
                || request.getIsActive() != null;
        if (!hasField) {
            throw new ValidationException(
                    MessageConstants.VALIDATION_FAILED, List.of(MessageConstants.SCHOLARSHIP_UPDATE_EMPTY));
        }
    }

    private AcademicYear findAcademicYearOrThrow(Long academicYearId) {
        return academicYearRepository
                .findByIdAndDeletedFalse(academicYearId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.ACADEMIC_YEAR_NOT_FOUND));
    }

    private FeeType resolveFeeType(ApplicableTo applicableTo, Long feeTypeId) {
        if (!ApplicableTo.SPECIFIC_HEAD.equals(applicableTo) || feeTypeId == null) {
            return null;
        }
        return feeTypeRepository
                .findByIdAndDeletedFalse(feeTypeId)
                .orElse(null);
    }

    private SchoolScheme findActiveSchemeOrThrow(Long schemeId) {
        return schoolSchemeRepository
                .findByIdAndIsActiveTrueAndDeletedFalse(schemeId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.SCHOLARSHIP_NOT_FOUND));
    }

    private SchoolScheme findSchemeOrThrow(Long schemeId) {
        return schoolSchemeRepository
                .findByIdAndDeletedFalse(schemeId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.SCHOLARSHIP_NOT_FOUND));
    }

    private AuthenticatedUser currentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser principal) {
            return principal;
        }
        throw new UnauthorizedException(MessageConstants.AUTHENTICATION_FAILED);
    }
}
