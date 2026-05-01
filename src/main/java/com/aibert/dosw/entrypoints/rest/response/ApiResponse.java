package com.aibert.dosw.entrypoints.rest.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * Standardized API response wrapper for all REST endpoints.
 * Provides a consistent structure for both success and error responses.
 *
 * @param <T> The type of the data payload
 */
@Getter
@Builder
public class ApiResponse<T> {
    private final String message;
    private final T data;
    private final String path;
    private final LocalDateTime timestamp;
    private final List<String> errors;

    /**
     * Creates a success response with data.
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a standard error response.
     */
    public static <T> ApiResponse<T> error(String message, String path) {
        return ApiResponse.<T>builder()
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a validation error response with a list of specific errors.
     */
    public static <T> ApiResponse<T> validationError(String message, List<String> errors, String path) {
        return ApiResponse.<T>builder()
                .message(message)
                .errors(errors)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
