package com.infiniteVision.schoolProject.modules.scholarship.service.impl;

import com.infiniteVision.schoolProject.common.audit.enums.AuditEntityType;
import com.infiniteVision.schoolProject.common.audit.service.AuditService;
import com.infiniteVision.schoolProject.common.dto.response.PagedResponseDTO;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.exception.BusinessException;
import com.infiniteVision.schoolProject.exception.ResourceNotFoundException;
import com.infiniteVision.schoolProject.exception.UnauthorizedException;
import com.infiniteVision.schoolProject.exception.ValidationException;
import com.infiniteVision.schoolProject.modules.academic.entity.AcademicYear;
import com.infiniteVision.schoolProject.modules.academic.repository.AcademicYearRepository;
import com.infiniteVision.schoolProject.modules.auth.enums.UserRole;
import com.infiniteVision.schoolProject.modules.scholarship.dto.request.BulkScholarshipApprovalRequestDTO;
import com.infiniteVision.schoolProject.modules.scholarship.dto.request.BulkScholarshipRejectRequestDTO;
import com.infiniteVision.schoolProject.modules.scholarship.dto.request.CreateScholarshipApplicationRequestDTO;
import com.infiniteVision.schoolProject.modules.scholarship.dto.request.RejectScholarshipApplicationRequestDTO;
import com.infiniteVision.schoolProject.modules.scholarship.dto.response.BulkScholarshipApplicationResponseDTO;
import com.infiniteVision.schoolProject.modules.scholarship.dto.response.ScholarshipApplicationActionResultDTO;
import com.infiniteVision.schoolProject.modules.scholarship.dto.response.ScholarshipApplicationResponseDTO;
import com.infiniteVision.schoolProject.modules.scholarship.entity.SchoolScheme;
import com.infiniteVision.schoolProject.modules.scholarship.entity.StudentScholarshipApplication;
import com.infiniteVision.schoolProject.modules.scholarship.enums.ScholarshipApplicationStatus;
import com.infiniteVision.schoolProject.modules.scholarship.mapper.ScholarshipApplicationMapper;
import com.infiniteVision.schoolProject.modules.scholarship.repository.SchoolSchemeRepository;
import com.infiniteVision.schoolProject.modules.scholarship.repository.StudentScholarshipApplicationRepository;
import com.infiniteVision.schoolProject.modules.dashboard.enums.DashboardActivityType;
import com.infiniteVision.schoolProject.modules.dashboard.service.DashboardActivityPublisher;
import com.infiniteVision.schoolProject.modules.notification.service.ScholarshipNotificationDispatcher;
import com.infiniteVision.schoolProject.modules.scholarship.dto.response.MeritBandResolveResponseDTO;
import com.infiniteVision.schoolProject.modules.scholarship.enums.ScholarshipApprovalAction;
import com.infiniteVision.schoolProject.modules.scholarship.service.MeritScholarshipBandService;
import com.infiniteVision.schoolProject.modules.scholarship.service.ScholarshipApplicationService;
import com.infiniteVision.schoolProject.modules.scholarship.service.ScholarshipApprovalHistoryService;
import com.infiniteVision.schoolProject.modules.scholarship.service.ScholarshipDiscountService;
import java.math.BigDecimal;
import com.infiniteVision.schoolProject.modules.student.entity.Student;
import com.infiniteVision.schoolProject.modules.student.repository.StudentRepository;
import com.infiniteVision.schoolProject.security.AuthenticatedUser;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manages scholarship discount applications: single-step approval by Principal or Correspondent,
 * bulk actions, and ledger recalculation on approval. Full fee payment is never blocked.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScholarshipApplicationServiceImpl implements ScholarshipApplicationService {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 50;

    private final StudentRepository studentRepository;
    private final AcademicYearRepository academicYearRepository;
    private final SchoolSchemeRepository schoolSchemeRepository;
    private final StudentScholarshipApplicationRepository applicationRepository;
    private final ScholarshipApplicationMapper applicationMapper;
    private final ScholarshipDiscountService scholarshipDiscountService;
    private final AuditService auditService;
    private final DashboardActivityPublisher dashboardActivityPublisher;
    private final MeritScholarshipBandService meritScholarshipBandService;
    private final ScholarshipApprovalHistoryService approvalHistoryService;
    private final ScholarshipNotificationDispatcher scholarshipNotificationDispatcher;

    /**
     * Staff submits a discount application for a student and scheme.
     */
    @Override
    @Transactional
    public ScholarshipApplicationResponseDTO createApplication(CreateScholarshipApplicationRequestDTO request) {
        Student student = studentRepository
                .findByIdAndDeletedFalse(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.STUDENT_NOT_FOUND));

        if (student.getAcademicYearId() == null
                || !student.getAcademicYearId().equals(request.getAcademicYearId())) {
            throw new ValidationException(
                    MessageConstants.VALIDATION_FAILED,
                    List.of(MessageConstants.CLASS_ACADEMIC_YEAR_MISMATCH));
        }

        AcademicYear academicYear = academicYearRepository
                .findByIdAndDeletedFalse(request.getAcademicYearId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.ACADEMIC_YEAR_NOT_FOUND));

        SchoolScheme scheme = schoolSchemeRepository
                .findByIdAndIsActiveTrueAndDeletedFalse(request.getSchemeId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.SCHOLARSHIP_NOT_FOUND));

        if (scheme.getAcademicYear() == null
                || !scheme.getAcademicYear().getId().equals(request.getAcademicYearId())) {
            throw new ValidationException(
                    MessageConstants.VALIDATION_FAILED,
                    List.of(MessageConstants.SCHOLARSHIP_SCHEME_YEAR_MISMATCH));
        }

        if (applicationRepository.existsByStudent_IdAndScheme_IdAndAcademicYear_IdAndDeletedFalse(
                request.getStudentId(), request.getSchemeId(), request.getAcademicYearId())) {
            throw new BusinessException(MessageConstants.SCHOLARSHIP_APPLICATION_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
        }

        BigDecimal marks = request.getMarksAtApplication() != null
                ? request.getMarksAtApplication()
                : student.getTenthMark();
        BigDecimal requestedPercent = null;
        if (marks != null) {
            MeritBandResolveResponseDTO merit =
                    meritScholarshipBandService.resolveDiscountPercent(request.getAcademicYearId(), marks);
            if (merit.isMatched()) {
                requestedPercent = merit.getDiscountPercent();
            }
        }

        StudentScholarshipApplication application = StudentScholarshipApplication.builder()
                .student(student)
                .scheme(scheme)
                .academicYear(academicYear)
                .status(ScholarshipApplicationStatus.PENDING)
                .appliedAt(LocalDateTime.now())
                .applicationRemarks(request.getApplicationRemarks())
                .marksAtApplication(marks)
                .requestedDiscountPercent(requestedPercent)
                .deleted(Boolean.FALSE)
                .build();

        StudentScholarshipApplication saved = applicationRepository.save(application);
        ScholarshipApplicationResponseDTO response = applicationMapper.toResponse(reload(saved.getId()));
        auditService.logCreate(AuditEntityType.SCHOLARSHIP_APPLICATION, saved.getId(), response);
        log.info(
                "Scholarship application created id={} studentId={} schemeId={}",
                saved.getId(),
                student.getId(),
                scheme.getId());
        dashboardActivityPublisher.publish(
                DashboardActivityType.SCHOLARSHIP_REQUEST,
                "Scholarship application submitted",
                student.getFirstName() + " applied for " + scheme.getSchemeName(),
                saved.getId(),
                null);
        approvalHistoryService.record(
                saved.getId(),
                ScholarshipApprovalAction.SUBMITTED,
                String.valueOf(currentUser().getUserId()),
                request.getApplicationRemarks(),
                null,
                ScholarshipApplicationStatus.PENDING);
        scholarshipNotificationDispatcher.notifyScholarshipEvent(
                reload(saved.getId()), ScholarshipApprovalAction.SUBMITTED, currentUser().getUsername());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<ScholarshipApplicationResponseDTO> listApplications(
            ScholarshipApplicationStatus status, Long studentId, Long academicYearId, int page, int size) {
        validatePagination(page, size);
        int effectiveSize = size > 0 ? Math.min(size, MAX_PAGE_SIZE) : DEFAULT_PAGE_SIZE;
        Pageable pageable = PageRequest.of(page, effectiveSize, Sort.by("appliedAt").descending());

        Page<StudentScholarshipApplication> applicationPage =
                applicationRepository.findAllFiltered(status, studentId, academicYearId, pageable);

        List<ScholarshipApplicationResponseDTO> content =
                applicationPage.getContent().stream().map(applicationMapper::toResponse).toList();

        return PagedResponseDTO.<ScholarshipApplicationResponseDTO>builder()
                .content(content)
                .page(applicationPage.getNumber())
                .size(applicationPage.getSize())
                .totalElements(applicationPage.getTotalElements())
                .totalPages(applicationPage.getTotalPages())
                .first(applicationPage.isFirst())
                .last(applicationPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ScholarshipApplicationResponseDTO getApplicationById(Long applicationId) {
        return applicationMapper.toResponse(findActiveWithRelationsOrThrow(applicationId));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<ScholarshipApplicationResponseDTO> listByStudent(Long studentId, int page, int size) {
        if (!studentRepository.findByIdAndDeletedFalse(studentId).isPresent()) {
            throw new ResourceNotFoundException(MessageConstants.STUDENT_NOT_FOUND);
        }
        return listApplications(null, studentId, null, page, size);
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

    /**
     * Principal or Correspondent approves in one step; triggers ledger discount recalculation.
     */
    @Override
    @Transactional
    public ScholarshipApplicationResponseDTO approve(Long applicationId) {
        ScholarshipApplicationActionResultDTO result = approveOne(applicationId, currentUser());
        if (!result.isSuccess()) {
            throw new BusinessException(result.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return applicationMapper.toResponse(findActiveWithRelationsOrThrow(applicationId));
    }

    @Override
    @Transactional
    public ScholarshipApplicationResponseDTO reject(Long applicationId, RejectScholarshipApplicationRequestDTO request) {
        ScholarshipApplicationActionResultDTO result =
                rejectOne(applicationId, request.getRejectionReason(), currentUser());
        if (!result.isSuccess()) {
            throw new BusinessException(result.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return applicationMapper.toResponse(findActiveWithRelationsOrThrow(applicationId));
    }

    /**
     * Bulk approve with per-item results (partial success allowed).
     */
    @Override
    @Transactional
    public BulkScholarshipApplicationResponseDTO bulkApprove(BulkScholarshipApprovalRequestDTO request) {
        AuthenticatedUser caller = currentUser();
        Set<Long> uniqueIds = new LinkedHashSet<>(request.getApplicationIds());
        List<ScholarshipApplicationActionResultDTO> results = new ArrayList<>();
        int success = 0;
        int skipped = 0;
        for (Long applicationId : uniqueIds) {
            ScholarshipApplicationActionResultDTO result = approveOne(applicationId, caller);
            results.add(result);
            if (result.isSuccess()) {
                success++;
            } else {
                skipped++;
            }
        }
        return buildBulkResponse(uniqueIds.size(), success, skipped, results);
    }

    @Override
    @Transactional
    public BulkScholarshipApplicationResponseDTO bulkReject(BulkScholarshipRejectRequestDTO request) {
        AuthenticatedUser caller = currentUser();
        Set<Long> uniqueIds = new LinkedHashSet<>(request.getApplicationIds());
        List<ScholarshipApplicationActionResultDTO> results = new ArrayList<>();
        int success = 0;
        int skipped = 0;
        for (Long applicationId : uniqueIds) {
            ScholarshipApplicationActionResultDTO result =
                    rejectOne(applicationId, request.getRejectionReason(), caller);
            results.add(result);
            if (result.isSuccess()) {
                success++;
            } else {
                skipped++;
            }
        }
        return buildBulkResponse(uniqueIds.size(), success, skipped, results);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected ScholarshipApplicationActionResultDTO approveOne(Long applicationId, AuthenticatedUser caller) {
        try {
            StudentScholarshipApplication application = findActiveWithRelationsOrThrow(applicationId);
            ScholarshipApplicationStatus previous = application.getStatus();

            if (ScholarshipApplicationStatus.APPROVED.equals(previous)) {
                return skipResult(application, previous, MessageConstants.SCHOLARSHIP_APPLICATION_ALREADY_APPROVED);
            }
            if (ScholarshipApplicationStatus.REJECTED.equals(previous)) {
                return skipResult(application, previous, MessageConstants.SCHOLARSHIP_APPLICATION_ALREADY_REJECTED);
            }
            if (!ScholarshipApplicationStatus.PENDING.equals(previous)
                    && !ScholarshipApplicationStatus.PRINCIPAL_APPROVED.equals(previous)) {
                return skipResult(application, previous, MessageConstants.SCHOLARSHIP_APPLICATION_INVALID_STATE);
            }

            UserRole role = caller.getRole();
            if (!UserRole.PRINCIPAL.equals(role) && !UserRole.CORRESPONDENT.equals(role)) {
                return skipResult(application, previous, MessageConstants.SCHOLARSHIP_APPROVAL_ROLE_FORBIDDEN);
            }

            ScholarshipApplicationResponseDTO before = applicationMapper.toResponse(application);
            String userId = String.valueOf(caller.getUserId());
            LocalDateTime now = LocalDateTime.now();
            if (UserRole.PRINCIPAL.equals(role)) {
                application.setPrincipalApprovedBy(userId);
                application.setPrincipalApprovedAt(now);
            } else {
                application.setCorrespondentApprovedBy(userId);
                application.setCorrespondentApprovedAt(now);
            }
            application.setStatus(ScholarshipApplicationStatus.APPROVED);
            application.setApprovedDiscountPercent(
                    application.getRequestedDiscountPercent() != null
                            ? application.getRequestedDiscountPercent()
                            : (application.getScheme() != null
                                    ? application.getScheme().getDiscountValue()
                                    : null));

            StudentScholarshipApplication saved = applicationRepository.save(application);
            scholarshipDiscountService.recalculateUnpaidLedgersForStudent(
                    saved.getStudent().getId(), saved.getAcademicYear().getId());

            ScholarshipApplicationResponseDTO after = applicationMapper.toResponse(reload(saved.getId()));
            auditService.logUpdate(
                    AuditEntityType.SCHOLARSHIP_APPLICATION, saved.getId(), before, after);
            approvalHistoryService.record(
                    saved.getId(),
                    ScholarshipApprovalAction.APPROVED,
                    userId,
                    null,
                    previous,
                    ScholarshipApplicationStatus.APPROVED);
            scholarshipNotificationDispatcher.notifyScholarshipEvent(
                    saved, ScholarshipApprovalAction.APPROVED, caller.getUsername());

            return ScholarshipApplicationActionResultDTO.builder()
                    .applicationId(saved.getId())
                    .studentId(saved.getStudent().getId())
                    .previousStatus(previous)
                    .newStatus(saved.getStatus())
                    .success(true)
                    .message(MessageConstants.SCHOLARSHIP_APPLICATION_APPROVED_SUCCESS)
                    .build();
        } catch (ResourceNotFoundException exception) {
            return ScholarshipApplicationActionResultDTO.builder()
                    .applicationId(applicationId)
                    .success(false)
                    .message(MessageConstants.SCHOLARSHIP_APPLICATION_NOT_FOUND)
                    .build();
        } catch (RuntimeException exception) {
            log.warn("Bulk approve failed for applicationId={}", applicationId, exception);
            return ScholarshipApplicationActionResultDTO.builder()
                    .applicationId(applicationId)
                    .success(false)
                    .message(exception.getMessage())
                    .build();
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected ScholarshipApplicationActionResultDTO rejectOne(
            Long applicationId, String rejectionReason, AuthenticatedUser caller) {
        try {
            if (!UserRole.PRINCIPAL.equals(caller.getRole()) && !UserRole.CORRESPONDENT.equals(caller.getRole())) {
                return ScholarshipApplicationActionResultDTO.builder()
                        .applicationId(applicationId)
                        .success(false)
                        .message(MessageConstants.SCHOLARSHIP_APPROVAL_ROLE_FORBIDDEN)
                        .build();
            }

            StudentScholarshipApplication application = findActiveWithRelationsOrThrow(applicationId);
            ScholarshipApplicationStatus previous = application.getStatus();

            if (ScholarshipApplicationStatus.APPROVED.equals(previous)) {
                return skipResult(application, previous, MessageConstants.SCHOLARSHIP_APPLICATION_ALREADY_APPROVED);
            }
            if (ScholarshipApplicationStatus.REJECTED.equals(previous)) {
                return skipResult(application, previous, MessageConstants.SCHOLARSHIP_APPLICATION_ALREADY_REJECTED);
            }
            if (!ScholarshipApplicationStatus.PENDING.equals(previous)
                    && !ScholarshipApplicationStatus.PRINCIPAL_APPROVED.equals(previous)) {
                return skipResult(application, previous, MessageConstants.SCHOLARSHIP_APPLICATION_INVALID_STATE);
            }

            ScholarshipApplicationResponseDTO before = applicationMapper.toResponse(application);
            application.setStatus(ScholarshipApplicationStatus.REJECTED);
            application.setRejectedBy(String.valueOf(caller.getUserId()));
            application.setRejectedAt(LocalDateTime.now());
            application.setRejectionReason(rejectionReason);

            StudentScholarshipApplication saved = applicationRepository.save(application);
            ScholarshipApplicationResponseDTO after = applicationMapper.toResponse(reload(saved.getId()));
            auditService.logUpdate(
                    AuditEntityType.SCHOLARSHIP_APPLICATION, saved.getId(), before, after);
            approvalHistoryService.record(
                    saved.getId(),
                    ScholarshipApprovalAction.REJECTED,
                    String.valueOf(caller.getUserId()),
                    rejectionReason,
                    previous,
                    ScholarshipApplicationStatus.REJECTED);
            scholarshipNotificationDispatcher.notifyScholarshipEvent(
                    saved, ScholarshipApprovalAction.REJECTED, caller.getUsername());

            return ScholarshipApplicationActionResultDTO.builder()
                    .applicationId(saved.getId())
                    .studentId(saved.getStudent().getId())
                    .previousStatus(previous)
                    .newStatus(ScholarshipApplicationStatus.REJECTED)
                    .success(true)
                    .message(MessageConstants.SCHOLARSHIP_APPLICATION_REJECTED_SUCCESS)
                    .build();
        } catch (ResourceNotFoundException exception) {
            return ScholarshipApplicationActionResultDTO.builder()
                    .applicationId(applicationId)
                    .success(false)
                    .message(MessageConstants.SCHOLARSHIP_APPLICATION_NOT_FOUND)
                    .build();
        }
    }

    private ScholarshipApplicationActionResultDTO skipResult(
            StudentScholarshipApplication application,
            ScholarshipApplicationStatus previous,
            String message) {
        return ScholarshipApplicationActionResultDTO.builder()
                .applicationId(application.getId())
                .studentId(application.getStudent() != null ? application.getStudent().getId() : null)
                .previousStatus(previous)
                .newStatus(application.getStatus())
                .success(false)
                .message(message)
                .build();
    }

    private BulkScholarshipApplicationResponseDTO buildBulkResponse(
            int requested, int success, int skipped, List<ScholarshipApplicationActionResultDTO> results) {
        return BulkScholarshipApplicationResponseDTO.builder()
                .requestedCount(requested)
                .successCount(success)
                .skippedCount(skipped)
                .failedCount(0)
                .results(results)
                .build();
    }

    private StudentScholarshipApplication findActiveWithRelationsOrThrow(Long applicationId) {
        return applicationRepository
                .findActiveWithRelationsById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.SCHOLARSHIP_APPLICATION_NOT_FOUND));
    }

    private StudentScholarshipApplication reload(Long applicationId) {
        return findActiveWithRelationsOrThrow(applicationId);
    }

    private AuthenticatedUser currentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser principal) {
            return principal;
        }
        throw new UnauthorizedException(MessageConstants.AUTHENTICATION_FAILED);
    }
}
