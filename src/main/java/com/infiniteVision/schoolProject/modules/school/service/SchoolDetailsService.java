package com.infiniteVision.schoolProject.modules.school.service;

import com.infiniteVision.schoolProject.common.dto.response.PagedResponseDTO;
import com.infiniteVision.schoolProject.modules.school.dto.request.CreateSchoolDetailsRequestDTO;
import com.infiniteVision.schoolProject.modules.school.dto.request.UpdateSchoolDetailsRequestDTO;
import com.infiniteVision.schoolProject.modules.school.dto.response.SchoolDetailsResponseDTO;

/**
 * School profile lifecycle: get, create, update, and soft delete.
 */
public interface SchoolDetailsService {

    /**
     * Paginated list of schools.
     *
     * @param activeOnly when true, only active rows
     * @param page zero-based page index
     * @param size page size (capped)
     * @return paginated school list
     */
    PagedResponseDTO<SchoolDetailsResponseDTO> listSchools(boolean activeOnly, int page, int size);

    /**
     * Load one school profile.
     *
     * @param schoolId school id
     * @return school details
     */
    SchoolDetailsResponseDTO getSchoolById(Long schoolId);

    /**
     * Create school profile.
     *
     * @param request create payload
     * @return created details
     */
    SchoolDetailsResponseDTO createSchool(CreateSchoolDetailsRequestDTO request);

    /**
     * Update school profile with partial fields.
     *
     * @param schoolId school id
     * @param request update payload
     * @return updated details
     */
    SchoolDetailsResponseDTO updateSchool(Long schoolId, UpdateSchoolDetailsRequestDTO request);

    /**
     * Soft delete school profile.
     *
     * @param schoolId school id
     */
    void deleteSchool(Long schoolId);
}
