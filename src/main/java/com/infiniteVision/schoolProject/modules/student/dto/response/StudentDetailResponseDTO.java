package com.infiniteVision.schoolProject.modules.student.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Full student view including profile, parents, and documents.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDetailResponseDTO {

    private StudentProfileResponseDTO student;
    private StudentParentsResponseDTO parents;
    private StudentDocumentsResponseDTO documents;
}
