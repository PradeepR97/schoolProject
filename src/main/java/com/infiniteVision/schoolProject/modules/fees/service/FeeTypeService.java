package com.infiniteVision.schoolProject.modules.fees.service;

import com.infiniteVision.schoolProject.modules.fees.dto.request.BulkCreateFeeTypeRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.request.CreateFeeTypeRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.request.UpdateFeeTypeRequestDTO;
import com.infiniteVision.schoolProject.modules.fees.dto.response.FeeTypeResponseDTO;
import java.util.List;

/**
 * Fee type master data lifecycle (tuition, transport, exam, etc.).
 */
public interface FeeTypeService {

    List<FeeTypeResponseDTO> listFeeTypes(boolean activeOnly);

    FeeTypeResponseDTO getFeeTypeById(Long id);

    FeeTypeResponseDTO createFeeType(CreateFeeTypeRequestDTO request);

    List<FeeTypeResponseDTO> bulkCreateFeeTypes(BulkCreateFeeTypeRequestDTO request);

    FeeTypeResponseDTO updateFeeType(Long id, UpdateFeeTypeRequestDTO request);

    void deleteFeeType(Long id);
}
