package com.infiniteVision.schoolProject.modules.scholarship.service;

import com.infiniteVision.schoolProject.modules.scholarship.dto.request.CreateScholarshipRequestDTO;
import com.infiniteVision.schoolProject.modules.scholarship.dto.request.UpdateScholarshipRequestDTO;
import com.infiniteVision.schoolProject.modules.scholarship.dto.response.ScholarshipResponseDTO;
import java.util.List;

/**
 * Scholarship scheme management operations.
 */
public interface ScholarshipService {

    ScholarshipResponseDTO createScholarship(CreateScholarshipRequestDTO request);

    List<ScholarshipResponseDTO> listActiveScholarships();

    ScholarshipResponseDTO getScholarshipById(Long schemeId);

    ScholarshipResponseDTO updateScholarship(Long schemeId, UpdateScholarshipRequestDTO request);

    void deactivateScholarship(Long schemeId);
}
