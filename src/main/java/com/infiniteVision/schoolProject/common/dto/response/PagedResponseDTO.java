package com.infiniteVision.schoolProject.common.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Paginated list wrapper for REST list endpoints.
 *
 * @param <T> item type in {@link #content}
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponseDTO<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
}
