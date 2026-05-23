package com.infiniteVision.schoolProject.modules.lookup.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Single dropdown option: {@code id} for UI selection, {@code label} for display, {@code value} for API submit.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LookupOptionDTO {

    private Long id;
    private String label;
    private String value;
}
