package com.infiniteVision.schoolProject.common.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Standard error envelope for REST APIs.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private boolean success;
    private String message;
    private List<String> errors;
    private LocalDateTime timestamp;

    public static ErrorResponse of(String message) {
        return of(message, Collections.emptyList());
    }

    public static ErrorResponse of(String message, List<String> errors) {
        return ErrorResponse.builder()
                .success(false)
                .message(message)
                .errors(errors == null || errors.isEmpty() ? null : List.copyOf(errors))
                .timestamp(LocalDateTime.now())
                .build();
    }
}
