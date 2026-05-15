package com.aibert.dosw.entrypoints.rest.response;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Standard API response wrapper for successful and error responses")
public class ApiResponse<T> {
    @Schema(description = "Human-readable response message", example = "Weekly distribution generated successfully")
    private final String message;

    @Schema(description = "Response payload for successful requests", nullable = true)
    private final T data;

    @Schema(description = "Request path that generated the response", example = "/planning/distribution")
    private final String path;

    @Schema(description = "Server timestamp for the response", example = "2026-05-12T20:00:00")
    private final LocalDateTime timestamp;

    @Schema(description = "List of validation or processing errors", example = "[\"studentId is required\"]")
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
