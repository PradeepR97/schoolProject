package com.infiniteVision.schoolProject.modules.fees.service.impl;

import com.infiniteVision.schoolProject.common.audit.enums.AuditEntityType;
import com.infiniteVision.schoolProject.common.audit.service.AuditService;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.exception.ResourceNotFoundException;
import com.infiniteVision.schoolProject.exception.UnauthorizedException;
import com.infiniteVision.schoolProject.modules.fees.dto.request.BulkCreateFeeHeadRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.request.CreateFeeHeadRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.request.UpdateFeeHeadRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.response.FeeHeadResponseDTO;
import com.infiniteVision.schoolProject.modules.fees.entity.FeeHead;
import com.infiniteVision.schoolProject.modules.fees.mapper.FeeHeadMapper;
import com.infiniteVision.schoolProject.modules.fees.repository.FeeHeadRepository;
import com.infiniteVision.schoolProject.modules.fees.service.FeeHeadService;
import com.infiniteVision.schoolProject.modules.fees.validator.FeeHeadValidator;
import com.infiniteVision.schoolProject.security.AuthenticatedUser;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Fee head master CRUD and bulk import for multiple payment categories (tuition, transport, etc.).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeeHeadServiceImpl implements FeeHeadService {

    private final FeeHeadRepository feeHeadRepository;
    private final FeeHeadMapper feeHeadMapper;
    private final FeeHeadValidator feeHeadValidator;
    private final AuditService auditService;

    /**
     * List all fee heads ordered for UI dropdowns; optional active-only filter.
     */
    @Override
    @Transactional(readOnly = true)
    public List<FeeHeadResponseDTO> listFeeHeads(boolean activeOnly) {
        List<FeeHead> heads = activeOnly
                ? feeHeadRepository.findAllByActiveTrueAndDeletedFalseOrderByDisplayOrderAscFeeHeadNameAsc()
                : feeHeadRepository.findAllByDeletedFalseOrderByDisplayOrderAscFeeHeadNameAsc();
        return heads.stream().map(feeHeadMapper::toResponse).toList();
    }

    /**
     * Load one fee head by id.
     */
    @Override
    @Transactional(readOnly = true)
    public FeeHeadResponseDTO getFeeHeadById(Long id) {
        return feeHeadMapper.toResponse(findActiveOrThrow(id));
    }

    /**
     * Create a single fee head after code uniqueness validation.
     */
    @Override
    @Transactional
    public FeeHeadResponseDTO createFeeHead(CreateFeeHeadRequestDTO request) {
        AuthenticatedUser caller = currentUser();
        feeHeadValidator.validateCreate(request);
        FeeHead saved = feeHeadRepository.save(feeHeadMapper.toEntity(request));
        FeeHeadResponseDTO response = feeHeadMapper.toResponse(saved);
        auditService.logCreate(AuditEntityType.FEE_HEAD, saved.getId(), response);
        log.info("Fee head created id={} code={} by user id={}", saved.getId(), saved.getFeeHeadCode(), caller.getUserId());
        return response;
    }

    /**
     * Import multiple fee heads (e.g. tuition, exam, transport) in one transaction.
     */
    @Override
    @Transactional
    public List<FeeHeadResponseDTO> bulkCreateFeeHeads(BulkCreateFeeHeadRequestDTO request) {
        List<FeeHeadResponseDTO> created = new ArrayList<>();
        for (CreateFeeHeadRequestDTO row : request.getFeeHeads()) {
            created.add(createFeeHead(row));
        }
        log.info("Bulk fee heads created count={}", created.size());
        return created;
    }

    /**
     * Partial update of fee head fields.
     */
    @Override
    @Transactional
    public FeeHeadResponseDTO updateFeeHead(Long id, UpdateFeeHeadRequestDTO request) {
        AuthenticatedUser caller = currentUser();
        FeeHead existing = findActiveOrThrow(id);
        FeeHeadResponseDTO before = feeHeadMapper.toResponse(existing);
        feeHeadValidator.validateUpdate(id, request, existing);
        feeHeadMapper.applyUpdates(existing, request);
        FeeHead saved = feeHeadRepository.save(existing);
        FeeHeadResponseDTO after = feeHeadMapper.toResponse(saved);
        auditService.logUpdate(AuditEntityType.FEE_HEAD, id, before, after);
        log.info("Fee head updated id={} by user id={}", id, caller.getUserId());
        return after;
    }

    /**
     * Soft-delete fee head and mark inactive.
     */
    @Override
    @Transactional
    public void deleteFeeHead(Long id) {
        AuthenticatedUser caller = currentUser();
        FeeHead head = findActiveOrThrow(id);
        FeeHeadResponseDTO before = feeHeadMapper.toResponse(head);
        head.markDeleted(caller.getUserId());
        head.setActive(Boolean.FALSE);
        feeHeadRepository.save(head);
        auditService.logDelete(AuditEntityType.FEE_HEAD, id, before);
        log.info("Fee head soft-deleted id={} by user id={}", id, caller.getUserId());
    }

    private FeeHead findActiveOrThrow(Long id) {
        return feeHeadRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.FEE_HEAD_NOT_FOUND));
    }

    private AuthenticatedUser currentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser principal) {
            return principal;
        }
        throw new UnauthorizedException(MessageConstants.AUTHENTICATION_FAILED);
    }
}
