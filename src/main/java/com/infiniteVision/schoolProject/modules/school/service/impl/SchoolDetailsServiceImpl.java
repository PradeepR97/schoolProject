package com.infiniteVision.schoolProject.modules.school.service.impl;

import com.infiniteVision.schoolProject.common.audit.enums.AuditEntityType;
import com.infiniteVision.schoolProject.common.audit.service.AuditService;
import com.infiniteVision.schoolProject.common.dto.response.PagedResponseDTO;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.exception.ResourceNotFoundException;
import com.infiniteVision.schoolProject.exception.UnauthorizedException;
import com.infiniteVision.schoolProject.exception.ValidationException;
import com.infiniteVision.schoolProject.modules.school.dto.request.CreateSchoolDetailsRequestDTO;
import com.infiniteVision.schoolProject.modules.school.dto.request.UpdateSchoolDetailsRequestDTO;
import com.infiniteVision.schoolProject.modules.school.dto.response.SchoolDetailsResponseDTO;
import com.infiniteVision.schoolProject.modules.school.entity.SchoolDetails;
import com.infiniteVision.schoolProject.modules.school.mapper.SchoolDetailsMapper;
import com.infiniteVision.schoolProject.modules.school.repository.SchoolDetailsRepository;
import com.infiniteVision.schoolProject.modules.school.service.SchoolDetailsService;
import com.infiniteVision.schoolProject.modules.school.validator.SchoolDetailsValidator;
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
 * School profile management for master details and billing configuration.
 * <p>
 * Flow: validate input, map request to entity, persist, and record audit trail.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SchoolDetailsServiceImpl implements SchoolDetailsService {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 50;

    private final SchoolDetailsRepository schoolDetailsRepository;
    private final SchoolDetailsMapper schoolDetailsMapper;
    private final SchoolDetailsValidator schoolDetailsValidator;
    private final AuditService auditService;

    /**
     * Load paginated list of non-deleted school profiles; default sort by school id descending.
     */
    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<SchoolDetailsResponseDTO> listSchools(boolean activeOnly, int page, int size) {
        validatePagination(page, size);
        int effectiveSize = size > 0 ? Math.min(size, MAX_PAGE_SIZE) : DEFAULT_PAGE_SIZE;
        Pageable pageable = PageRequest.of(page, effectiveSize, Sort.by("id").descending());

        Page<SchoolDetails> schoolPage = activeOnly
                ? schoolDetailsRepository.findAllByActiveTrueAndDeletedFalse(pageable)
                : schoolDetailsRepository.findAllByDeletedFalse(pageable);

        List<SchoolDetailsResponseDTO> content = schoolPage.getContent().stream()
                .map(schoolDetailsMapper::toResponse)
                .toList();

        return PagedResponseDTO.<SchoolDetailsResponseDTO>builder()
                .content(content)
                .page(schoolPage.getNumber())
                .size(schoolPage.getSize())
                .totalElements(schoolPage.getTotalElements())
                .totalPages(schoolPage.getTotalPages())
                .first(schoolPage.isFirst())
                .last(schoolPage.isLast())
                .build();
    }

    /**
     * Load one active school profile by id.
     */
    @Override
    @Transactional(readOnly = true)
    public SchoolDetailsResponseDTO getSchoolById(Long schoolId) {
        return schoolDetailsMapper.toResponse(findActiveOrThrow(schoolId));
    }

    /**
     * Validate and persist a new school profile.
     */
    @Override
    @Transactional
    public SchoolDetailsResponseDTO createSchool(CreateSchoolDetailsRequestDTO request) {
        AuthenticatedUser caller = currentUser();
        schoolDetailsValidator.validateCreate(request);
        SchoolDetails saved = schoolDetailsRepository.save(schoolDetailsMapper.toEntity(request));
        SchoolDetailsResponseDTO response = schoolDetailsMapper.toResponse(saved);
        auditService.logCreate(AuditEntityType.SCHOOL, saved.getId(), response);
        log.info("School created id={} by user id={}", saved.getId(), caller.getUserId());
        return response;
    }

    /**
     * Validate and apply partial updates to an existing school profile.
     */
    @Override
    @Transactional
    public SchoolDetailsResponseDTO updateSchool(Long schoolId, UpdateSchoolDetailsRequestDTO request) {
        AuthenticatedUser caller = currentUser();
        SchoolDetails existing = findActiveOrThrow(schoolId);
        SchoolDetailsResponseDTO before = schoolDetailsMapper.toResponse(existing);
        schoolDetailsValidator.validateUpdate(schoolId, request, existing);
        schoolDetailsMapper.applyUpdates(existing, request);
        SchoolDetails saved = schoolDetailsRepository.save(existing);
        SchoolDetailsResponseDTO after = schoolDetailsMapper.toResponse(saved);
        auditService.logUpdate(AuditEntityType.SCHOOL, schoolId, before, after);
        log.info("School updated id={} by user id={}", schoolId, caller.getUserId());
        return after;
    }

    /**
     * Soft delete school profile and mark it inactive.
     */
    @Override
    @Transactional
    public void deleteSchool(Long schoolId) {
        AuthenticatedUser caller = currentUser();
        SchoolDetails school = findActiveOrThrow(schoolId);
        SchoolDetailsResponseDTO before = schoolDetailsMapper.toResponse(school);
        school.softDelete(caller.getUserId());
        schoolDetailsRepository.save(school);
        auditService.logDelete(AuditEntityType.SCHOOL, schoolId, before);
        log.info("School deleted id={} by user id={}", schoolId, caller.getUserId());
    }

    private SchoolDetails findActiveOrThrow(Long schoolId) {
        return schoolDetailsRepository
                .findByIdAndDeletedFalse(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.SCHOOL_NOT_FOUND));
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
