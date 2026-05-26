package com.infiniteVision.schoolProject.modules.fees.dto.response;

import com.infiniteVision.schoolProject.modules.fees.enums.TermType;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Fee structure detail for list and get responses.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeStructureResponseDTO {

    private Long structureId;
    private Long academicYearId;
    private String academicYearName;
    private Long classId;
    private String className;
    private Long feeTypeId;
    private String feeTypeCode;
    private String feeTypeName;
    private TermType termType;
    private BigDecimal amount;
    private LocalDate dueDate;
    private BigDecimal lateFeeDaily;
    private BigDecimal maxLateFee;
    private Boolean scholarshipAllowed;
    private Boolean installmentAllowed;
    private Boolean active;
    private String remarks;
}
