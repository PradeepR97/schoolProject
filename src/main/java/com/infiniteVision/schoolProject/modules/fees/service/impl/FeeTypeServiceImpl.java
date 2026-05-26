package com.infiniteVision.schoolProject.modules.fees.service.impl;

import com.infiniteVision.schoolProject.common.audit.enums.AuditEntityType;
import com.infiniteVision.schoolProject.common.audit.service.AuditService;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.exception.ResourceNotFoundException;
import com.infiniteVision.schoolProject.exception.UnauthorizedException;
import com.infiniteVision.schoolProject.modules.fees.dto.request.BulkCreateFeeTypeRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.request.CreateFeeTypeRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.request.UpdateFeeTypeRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.response.FeeTypeResponseDTO;
import com.infiniteVision.schoolProject.modules.fees.entity.FeeType;
import com.infiniteVision.schoolProject.modules.fees.mapper.FeeTypeMapper;
import com.infiniteVision.schoolProject.modules.fees.repository.FeeTypeRepository;
import com.infiniteVision.schoolProject.modules.fees.service.FeeTypeService;
import com.infiniteVision.schoolProject.modules.fees.validator.FeeTypeValidator;
import com.infiniteVision.schoolProject.security.AuthenticatedUser;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Fee type master CRUD: list, get, create, bulk create, update, soft delete.
 * <p>
 * Fee types are referenced by fee structures, ledgers, invoices, and scholarship schemes.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeeTypeServiceImpl implements FeeTypeService {

    private final FeeTypeRepository feeTypeRepository;
    private final FeeTypeMapper feeTypeMapper;
    private final FeeTypeValidator feeTypeValidator;
    private final AuditService auditService;

    /**
     * List fee types ordered for UI dropdowns; optional active-only filter.
     */
    @Override
    @Transactional(readOnly = true)
    public List<FeeTypeResponseDTO> listFeeTypes(boolean activeOnly) {
        List<FeeType> types = activeOnly
                ? feeTypeRepository.findAllByActiveTrueAndDeletedFalseOrderByDisplayOrderAscFeeTypeNameAsc()
                : feeTypeRepository.findAllByDeletedFalseOrderByDisplayOrderAscFeeTypeNameAsc();
        return types.stream().map(feeTypeMapper::toResponse).toList();
    }

    /**
     * Load one active fee type by id.
     */
    @Override
    @Transactional(readOnly = true)
    public FeeTypeResponseDTO getFeeTypeById(Long id) {
        return feeTypeMapper.toResponse(findActiveOrThrow(id));
    }

    /**
     * Create a fee type after code uniqueness validation.
     */
    @Override
    @Transactional
    public FeeTypeResponseDTO createFeeType(CreateFeeTypeRequestDTO request) {
        AuthenticatedUser caller = currentUser();
        feeTypeValidator.validateCreate(request);
        FeeType saved = feeTypeRepository.save(feeTypeMapper.toEntity(request));
        FeeTypeResponseDTO response = feeTypeMapper.toResponse(saved);
        auditService.logCreate(AuditEntityType.FEE_TYPE, saved.getId(), response);
        log.info(
                "Fee type created id={} code={} by user id={}",
                saved.getId(),
                saved.getFeeTypeCode(),
                caller.getUserId());
        return response;
    }

    /**
     * Import multiple fee types in one transaction.
     */
    @Override
    @Transactional
    public List<FeeTypeResponseDTO> bulkCreateFeeTypes(BulkCreateFeeTypeRequestDTO request) {
        List<FeeTypeResponseDTO> created = new ArrayList<>();
        for (CreateFeeTypeRequestDTO row : request.getFeeTypes()) {
            created.add(createFeeType(row));
        }
        log.info("Bulk fee types created count={}", created.size());
        return created;
    }

    /**
     * Partial update of fee type fields.
     */
    @Override
    @Transactional
    public FeeTypeResponseDTO updateFeeType(Long id, UpdateFeeTypeRequestDTO request) {
        AuthenticatedUser caller = currentUser();
        FeeType existing = findActiveOrThrow(id);
        FeeTypeResponseDTO before = feeTypeMapper.toResponse(existing);
        feeTypeValidator.validateUpdate(id, request, existing);
        feeTypeMapper.applyUpdates(existing, request);
        FeeType saved = feeTypeRepository.save(existing);
        FeeTypeResponseDTO after = feeTypeMapper.toResponse(saved);
        auditService.logUpdate(AuditEntityType.FEE_TYPE, id, before, after);
        log.info("Fee type updated id={} by user id={}", id, caller.getUserId());
        return after;
    }

    /**
     * Soft-delete fee type and mark inactive.
     */
    @Override
    @Transactional
    public void deleteFeeType(Long id) {
        AuthenticatedUser caller = currentUser();
        FeeType feeType = findActiveOrThrow(id);
        FeeTypeResponseDTO before = feeTypeMapper.toResponse(feeType);
        feeType.markDeleted(caller.getUserId());
        feeType.setActive(Boolean.FALSE);
        feeTypeRepository.save(feeType);
        auditService.logDelete(AuditEntityType.FEE_TYPE, id, before);
        log.info("Fee type soft-deleted id={} by user id={}", id, caller.getUserId());
    }

    private FeeType findActiveOrThrow(Long id) {
        return feeTypeRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.FEE_TYPE_NOT_FOUND));
    }

    private AuthenticatedUser currentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser principal) {
            return principal;
        }
        throw new UnauthorizedException(MessageConstants.AUTHENTICATION_FAILED);
    }
}
