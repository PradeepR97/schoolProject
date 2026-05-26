package com.infiniteVision.schoolProject.modules.fees.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Class-wise fee matrix for admin UI (fee heads × term rows).
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeStructureMatrixResponseDTO {

    private Long academicYearId;
    private Long classId;
    private String className;
    private List<FeeStructureResponseDTO> structures;
}
