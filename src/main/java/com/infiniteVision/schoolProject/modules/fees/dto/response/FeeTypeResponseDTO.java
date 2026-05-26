package com.infiniteVision.schoolProject.modules.fees.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Fee type master record for list and detail APIs.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeTypeResponseDTO {

    private Long feeTypeId;
    private String feeTypeCode;
    private String feeTypeName;
    private Boolean active;
    private Integer displayOrder;
}
