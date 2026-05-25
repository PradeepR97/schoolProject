package com.infiniteVision.schoolProject.modules.fees.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Fee head master record for list and detail APIs.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeHeadResponseDTO {

    private Long feeHeadId;
    private String feeHeadCode;
    private String feeHeadName;
    private String description;
    private String feeCategory;
    private Boolean mandatory;
    private Boolean refundable;
    private Boolean active;
    private Integer displayOrder;
}
