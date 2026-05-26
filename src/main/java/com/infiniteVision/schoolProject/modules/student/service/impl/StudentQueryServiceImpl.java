package com.infiniteVision.schoolProject.modules.student.service.impl;

import com.infiniteVision.schoolProject.common.dto.response.PagedResponseDTO;
import com.infiniteVision.schoolProject.constants.MessageConstants;
import com.infiniteVision.schoolProject.exception.ValidationException;
import com.infiniteVision.schoolProject.modules.academic.entity.ClassMaster;
import com.infiniteVision.schoolProject.modules.academic.entity.SectionMaster;
import com.infiniteVision.schoolProject.modules.academic.repository.ClassMasterRepository;
import com.infiniteVision.schoolProject.modules.academic.repository.SectionMasterRepository;
import com.infiniteVision.schoolProject.modules.student.dto.response.StudentListItemResponseDTO;
import com.infiniteVision.schoolProject.modules.student.entity.Student;
import com.infiniteVision.schoolProject.modules.student.mapper.StudentListMapper;
import com.infiniteVision.schoolProject.modules.student.repository.StudentRepository;
import com.infiniteVision.schoolProject.modules.student.service.StudentQueryService;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Paginated student list for school staff screens.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StudentQueryServiceImpl implements StudentQueryService {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 50;

    private final StudentRepository studentRepository;
    private final ClassMasterRepository classMasterRepository;
    private final SectionMasterRepository sectionMasterRepository;
    private final StudentListMapper studentListMapper;

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<StudentListItemResponseDTO> listStudents(int page, int size) {
        validatePagination(page, size);
        int effectiveSize = size > 0 ? Math.min(size, MAX_PAGE_SIZE) : DEFAULT_PAGE_SIZE;

        Pageable pageable = PageRequest.of(page, effectiveSize, Sort.by("firstName").ascending());
        Page<Student> studentPage = studentRepository.findAllActiveWithParents(pageable);

        Map<Long, String> classNameByStudentId = resolveClassNames(studentPage.getContent());

        List<StudentListItemResponseDTO> content = studentPage.getContent().stream()
                .map(student -> studentListMapper.toListItem(
                        student, classNameByStudentId.get(student.getId())))
                .toList();

        log.info(
                "Students listed page={} size={} total={}",
                page,
                effectiveSize,
                studentPage.getTotalElements());

        return PagedResponseDTO.<StudentListItemResponseDTO>builder()
                .content(content)
                .page(studentPage.getNumber())
                .size(studentPage.getSize())
                .totalElements(studentPage.getTotalElements())
                .totalPages(studentPage.getTotalPages())
                .first(studentPage.isFirst())
                .last(studentPage.isLast())
                .build();
    }

    private Map<Long, String> resolveClassNames(List<Student> students) {
        Set<Long> classIds = students.stream()
                .map(Student::getClassId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<Long> sectionIds = students.stream()
                .map(Student::getSectionId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (classIds.isEmpty()) {
            return Map.of();
        }

        Map<Long, ClassMaster> classMasterById = classMasterRepository.findAllByIdInAndDeletedFalse(classIds).stream()
                .collect(Collectors.toMap(ClassMaster::getId, classMaster -> classMaster));

        Map<Long, SectionMaster> sectionById = sectionIds.isEmpty()
                ? Map.of()
                : sectionMasterRepository.findAllById(sectionIds).stream()
                        .filter(section -> !Boolean.TRUE.equals(section.getDeleted()))
                        .collect(Collectors.toMap(SectionMaster::getId, section -> section));

        return studentListMapper.toClassNameByStudentId(classMasterById, sectionById, students);
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
}
