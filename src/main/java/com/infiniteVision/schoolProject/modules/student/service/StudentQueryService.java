package com.infiniteVision.schoolProject.modules.student.service;

import com.infiniteVision.schoolProject.common.dto.response.PagedResponseDTO;
import com.infiniteVision.schoolProject.modules.student.dto.response.StudentListItemResponseDTO;

/**
 * Read-only student queries (paginated list for admin screens).
 */
public interface StudentQueryService {

    /**
     * Returns a page of active students with list summary fields.
     *
     * @param page zero-based page index
     * @param size page size (capped internally)
     * @return paginated list items
     */
    PagedResponseDTO<StudentListItemResponseDTO> listStudents(int page, int size);
}
