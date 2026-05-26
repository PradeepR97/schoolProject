package com.infiniteVision.schoolProject.modules.fees.dto.request;

import com.infiniteVision.schoolProject.modules.fees.enums.TermType;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Partial update body for a fee structure row.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFeeStructureRequestDTO {

    private Long academicYearId;

    private Long classId;

    private Long sectionId;

    private Long feeHeadId;

    private TermType termType;

    @PositiveOrZero(message = "Amount must be zero or positive")
    private BigDecimal amount;

    private LocalDate dueDate;

    @PositiveOrZero(message = "Late fee daily must be zero or positive")
    private BigDecimal lateFeeDaily;

    @PositiveOrZero(message = "Max late fee must be zero or positive")
    private BigDecimal maxLateFee;

    private Boolean scholarshipAllowed;

    private Boolean installmentAllowed;

    private Boolean active;

    private String remarks;
}
