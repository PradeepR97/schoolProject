package com.infiniteVision.schoolProject.modules.fees.service;

import com.infiniteVision.schoolProject.modules.fees.dto.request.BulkCreateFeeHeadRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.request.CreateFeeHeadRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.request.UpdateFeeHeadRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.response.FeeHeadResponseDTO;
import java.util.List;

/**
 * Fee head master data lifecycle.
 */
public interface FeeHeadService {

    List<FeeHeadResponseDTO> listFeeHeads(boolean activeOnly);

    FeeHeadResponseDTO getFeeHeadById(Long id);

    FeeHeadResponseDTO createFeeHead(CreateFeeHeadRequestDTO request);

    List<FeeHeadResponseDTO> bulkCreateFeeHeads(BulkCreateFeeHeadRequestDTO request);

    FeeHeadResponseDTO updateFeeHead(Long id, UpdateFeeHeadRequestDTO request);

    void deleteFeeHead(Long id);
}
