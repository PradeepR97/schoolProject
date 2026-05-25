package com.infiniteVision.schoolProject.modules.fees.dto.request;

import com.infiniteVision.schoolProject.modules.fees.enums.TermType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body to create a fee structure row.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFeeStructureRequestDTO {

    @NotNull(message = "Academic year id is required")
    private Long academicYearId;

    @NotNull(message = "Class id is required")
    private Long classId;

    @NotNull(message = "Fee head id is required")
    private Long feeHeadId;

    @NotNull(message = "Term type is required")
    private TermType termType;

    @NotNull(message = "Amount is required")
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
