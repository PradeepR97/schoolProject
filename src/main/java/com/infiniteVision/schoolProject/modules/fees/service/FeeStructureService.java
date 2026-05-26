package com.infiniteVision.schoolProject.modules.fees.service;

import com.infiniteVision.schoolProject.common.dto.response.PagedResponseDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.request.BulkCreateFeeStructureRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.request.CreateFeeStructureRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.request.UpdateFeeStructureRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.response.FeeStructureMatrixResponseDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.response.FeeStructureResponseDTO;
import java.util.List;

/**
 * Fee structure CRUD: list (filtered/paginated), get, create, update, soft delete.
 */
public interface FeeStructureService {

    /**
     * Paginated list of fee structures with optional filters.
     *
     * @param academicYearId optional filter
     * @param classId optional grade filter
     * @param sectionId optional section filter
     * @param feeHeadId optional filter
     * @param activeOnly when true, only active rows
     * @param page zero-based page
     * @param size page size
     * @return paginated fee structure rows
     */
    PagedResponseDTO<FeeStructureResponseDTO> listFeeStructures(
            Long academicYearId,
            Long classId,
            Long sectionId,
            Long feeHeadId,
            boolean activeOnly,
            int page,
            int size);

    /**
     * Returns one active fee structure by id.
     *
     * @param id structure primary key
     * @return detail DTO
     */
    FeeStructureResponseDTO getFeeStructureById(Long id);

    /**
     * Creates a new fee structure row.
     *
     * @param request create payload
     * @return created row
     */
    FeeStructureResponseDTO createFeeStructure(CreateFeeStructureRequestDTO request);

    /**
     * Partially updates a fee structure row.
     *
     * @param id structure primary key
     * @param request fields to update
     * @return updated row
     */
    FeeStructureResponseDTO updateFeeStructure(Long id, UpdateFeeStructureRequestDTO request);

    /**
     * Soft-deletes a fee structure row.
     *
     * @param id structure primary key
     */
    void deleteFeeStructure(Long id);

    /**
     * Returns all active fee structure rows for a grade, section, and academic year (matrix view).
     */
    FeeStructureMatrixResponseDTO getFeeStructureMatrix(Long academicYearId, Long classId, Long sectionId);

    /**
     * Creates multiple fee structure rows in one transaction.
     */
    List<FeeStructureResponseDTO> bulkCreateFeeStructures(BulkCreateFeeStructureRequestDTO request);
}
