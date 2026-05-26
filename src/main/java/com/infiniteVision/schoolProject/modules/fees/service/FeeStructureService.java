package com.infiniteVision.schoolProject.modules.fees.service;

import com.infiniteVision.schoolProject.common.dto.response.PagedResponseDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.request.BulkCreateFeeStructureRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.request.CreateFeeStructureRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.request.UpdateFeeStructureRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.response.FeeStructureMatrixResponseDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.response.FeeStructureResponseDTO;
import java.util.List;

/**
 * Fee structure management: amounts per academic year, class (grade), fee type, and term.
 */
public interface FeeStructureService {

    /**
     * Paginated list with optional filters.
     *
     * @param academicYearId optional filter
     * @param classId optional filter
     * @param feeTypeId optional filter
     * @param activeOnly when true, only active rows
     * @param page zero-based page index
     * @param size page size (capped)
     * @return paged fee structure rows
     */
    PagedResponseDTO<FeeStructureResponseDTO> listFeeStructures(
            Long academicYearId,
            Long classId,
            Long feeTypeId,
            boolean activeOnly,
            int page,
            int size);

    FeeStructureResponseDTO getFeeStructureById(Long id);

    FeeStructureResponseDTO createFeeStructure(CreateFeeStructureRequestDTO request);

    FeeStructureResponseDTO updateFeeStructure(Long id, UpdateFeeStructureRequestDTO request);

    void deleteFeeStructure(Long id);

    /**
     * Returns all active fee structure rows for a grade and academic year (matrix view).
     */
    FeeStructureMatrixResponseDTO getFeeStructureMatrix(Long academicYearId, Long classId);

    List<FeeStructureResponseDTO> bulkCreateFeeStructures(BulkCreateFeeStructureRequestDTO request);
}
