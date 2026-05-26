package com.infiniteVision.schoolProject.modules.fees.service.impl;

import com.infiniteVision.schoolProject.common.audit.enums.AuditEntityType;
import com.infiniteVision.schoolProject.common.audit.service.AuditService;
import com.infiniteVision.schoolProject.common.dto.response.PagedResponseDTO;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.exception.ResourceNotFoundException;
import com.infiniteVision.schoolProject.exception.UnauthorizedException;
import com.infiniteVision.schoolProject.exception.ValidationException;
import com.infiniteVision.schoolProject.modules.academic.entity.ClassMaster;
import com.infiniteVision.schoolProject.modules.academic.entity.SectionMaster;
import com.infiniteVision.schoolProject.modules.academic.repository.ClassMasterRepository;
import com.infiniteVision.schoolProject.modules.academic.repository.SectionMasterRepository;
import com.infiniteVision.schoolProject.modules.academic.util.ClassSectionDisplayFormatter;
import com.infiniteVision.schoolProject.modules.fees.dto.request.BulkCreateFeeStructureRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.request.CreateFeeStructureRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.request.UpdateFeeStructureRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.response.FeeStructureMatrixResponseDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.response.FeeStructureResponseDTO;
import com.infiniteVision.schoolProject.modules.fees.entity.FeeStructure;
import com.infiniteVision.schoolProject.modules.fees.mapper.FeeStructureMapper;
import com.infiniteVision.schoolProject.modules.fees.repository.FeeStructureRepository;
import com.infiniteVision.schoolProject.modules.fees.service.FeeStructureService;
import com.infiniteVision.schoolProject.modules.fees.validator.FeeStructureValidator;
import com.infiniteVision.schoolProject.security.AuthenticatedUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Fee structure lifecycle: filtered paginated list, get, create, partial update, soft delete.
 * <p>
 * Uniqueness: one row per (academic year, class, fee head, term type). References must align
 * (class belongs to academic year; fee head must exist).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeeStructureServiceImpl implements FeeStructureService {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 50;

    private final FeeStructureRepository feeStructureRepository;
    private final FeeStructureMapper feeStructureMapper;
    private final FeeStructureValidator feeStructureValidator;
    private final AuditService auditService;
    private final ClassMasterRepository classMasterRepository;
    private final SectionMasterRepository sectionMasterRepository;

    /**
     * Load filtered page of non-deleted fee structures; default sort by structure id descending.
     */
    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<FeeStructureResponseDTO> listFeeStructures(
            Long academicYearId,
            Long classId,
            Long sectionId,
            Long feeTypeId,
            boolean activeOnly,
            int page,
            int size) {
        validatePagination(page, size);
        int effectiveSize = size > 0 ? Math.min(size, MAX_PAGE_SIZE) : DEFAULT_PAGE_SIZE;

        Pageable pageable = PageRequest.of(page, effectiveSize, Sort.by("id").descending());
        Page<FeeStructure> structurePage = feeStructureRepository.findAllFiltered(
                academicYearId, classId, sectionId, feeTypeId, activeOnly, pageable);

        List<FeeStructureResponseDTO> content =
                structurePage.getContent().stream().map(feeStructureMapper::toResponse).toList();

        log.info(
                "Fee structures listed page={} size={} total={}",
                page,
                effectiveSize,
                structurePage.getTotalElements());

        return PagedResponseDTO.<FeeStructureResponseDTO>builder()
                .content(content)
                .page(structurePage.getNumber())
                .size(structurePage.getSize())
                .totalElements(structurePage.getTotalElements())
                .totalPages(structurePage.getTotalPages())
                .first(structurePage.isFirst())
                .last(structurePage.isLast())
                .build();
    }

    /**
     * Load one active fee structure with academic year, class, and fee head.
     */
    @Override
    @Transactional(readOnly = true)
    public FeeStructureResponseDTO getFeeStructureById(Long id) {
        return feeStructureMapper.toResponse(findActiveWithRelationsOrThrow(id));
    }

    /**
     * Validate create payload, persist fee structure, return mapped DTO.
     */
    @Override
    @Transactional
    public FeeStructureResponseDTO createFeeStructure(CreateFeeStructureRequestDTO request) {
        AuthenticatedUser caller = currentUser();
        feeStructureValidator.validateCreate(request);

        FeeStructure saved = feeStructureRepository.save(feeStructureMapper.toEntity(request));
        FeeStructureResponseDTO afterSnapshot =
                feeStructureMapper.toResponse(findActiveWithRelationsOrThrow(saved.getId()));
        auditService.logCreate(AuditEntityType.FEE_STRUCTURE, saved.getId(), afterSnapshot);
        log.info("Fee structure created id={} by user id={}", saved.getId(), caller.getUserId());

        return afterSnapshot;
    }

    /**
     * Validate partial update, apply fields, save, return detail with relations loaded.
     */
    @Override
    @Transactional
    public FeeStructureResponseDTO updateFeeStructure(Long id, UpdateFeeStructureRequestDTO request) {
        AuthenticatedUser caller = currentUser();
        FeeStructure existing = findActiveWithRelationsOrThrow(id);
        FeeStructureResponseDTO beforeSnapshot = feeStructureMapper.toResponse(existing);

        feeStructureValidator.validateUpdate(id, request, existing);
        feeStructureMapper.applyUpdates(existing, request);

        FeeStructure saved = feeStructureRepository.save(existing);
        FeeStructureResponseDTO afterSnapshot =
                feeStructureMapper.toResponse(findActiveWithRelationsOrThrow(saved.getId()));
        auditService.logUpdate(AuditEntityType.FEE_STRUCTURE, id, beforeSnapshot, afterSnapshot);
        log.info("Fee structure updated id={} by user id={}", id, caller.getUserId());

        return afterSnapshot;
    }

    /**
     * Soft-delete fee structure and set inactive.
     */
    @Override
    @Transactional
    public void deleteFeeStructure(Long id) {
        AuthenticatedUser caller = currentUser();
        FeeStructure structure = findActiveWithRelationsOrThrow(id);
        FeeStructureResponseDTO beforeSnapshot = feeStructureMapper.toResponse(structure);

        structure.softDelete(caller.getUserId());
        feeStructureRepository.save(structure);
        auditService.logDelete(AuditEntityType.FEE_STRUCTURE, id, beforeSnapshot);

        log.info("Fee structure soft-deleted id={} by user id={}", id, caller.getUserId());
    }

    /**
     * Loads active structures for class/year as a matrix payload for the fee document UI.
     */
    @Override
    @Transactional(readOnly = true)
    public FeeStructureMatrixResponseDTO getFeeStructureMatrix(Long academicYearId, Long classId, Long sectionId) {
        ClassMaster classMaster = classMasterRepository
                .findByIdAndDeletedFalse(classId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.CLASS_NOT_FOUND));
        SectionMaster section = sectionMasterRepository
                .findByIdAndDeletedFalse(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.SECTION_NOT_FOUND));

        List<FeeStructureResponseDTO> structures = feeStructureRepository
                .findActiveByClassIdAndSectionIdAndAcademicYearId(classId, sectionId, academicYearId)
                .stream()
                .map(feeStructureMapper::toResponse)
                .toList();

        return FeeStructureMatrixResponseDTO.builder()
                .academicYearId(academicYearId)
                .classId(classId)
                .sectionId(sectionId)
                .className(ClassSectionDisplayFormatter.format(classMaster, section))
                .structures(structures)
                .build();
    }

    /**
     * Persists each structure row; skips none — duplicates raise validation from repository layer.
     */
    @Override
    @Transactional
    public List<FeeStructureResponseDTO> bulkCreateFeeStructures(BulkCreateFeeStructureRequestDTO request) {
        List<FeeStructureResponseDTO> created = new java.util.ArrayList<>();
        for (CreateFeeStructureRequestDTO row : request.getStructures()) {
            created.add(createFeeStructure(row));
        }
        log.info("Bulk fee structures created count={}", created.size());
        return created;
    }

    private FeeStructure findActiveWithRelationsOrThrow(Long id) {
        return feeStructureRepository
                .findActiveWithRelationsById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.FEE_STRUCTURE_NOT_FOUND));
    }

    private void validatePagination(int page, int size) {
        if (page < 0) {
            throw new ValidationException(
                    MessageConstants.VALIDATION_FAILED, List.of("Page index must be zero or greater"));
        }
        if (size < 0) {
            throw new ValidationException(
                    MessageConstants.VALIDATION_FAILED, List.of("Page size must be zero or greater"));
        }
    }

    private AuthenticatedUser currentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser principal) {
            return principal;
        }
        throw new UnauthorizedException(MessageConstants.AUTHENTICATION_FAILED);
    }
}
