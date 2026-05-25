package com.infiniteVision.schoolProject.modules.student.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Root body for student admission: student profile, parents, and optional document metadata.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentAdmissionRequestDTO {

    @Valid
    @NotNull(message = "Student details are required")
    private StudentAdmissionStudentDTO student;

    @Valid
    @NotNull(message = "Parent details are required")
    private StudentAdmissionParentsDTO parents;

    @Valid
    private StudentAdmissionDocumentsDTO documents;
}
