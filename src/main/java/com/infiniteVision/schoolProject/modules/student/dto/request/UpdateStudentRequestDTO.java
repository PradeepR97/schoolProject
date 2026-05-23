package com.infiniteVision.schoolProject.modules.student.dto.request;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Partial update body for student aggregate (profile, parents, documents).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStudentRequestDTO {

    @Valid
    private UpdateStudentProfileDTO student;

    @Valid
    private UpdateStudentParentsDTO parents;

    @Valid
    private UpdateStudentDocumentsDTO documents;
}
