package in.gov.manipur.rccms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard API Response Wrapper
 * Used to wrap all API responses in a consistent format
 * 
 * @param <T> Type of the data payload
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /**
     * Indicates if the request was successful
     */
    private Boolean success;

    /**
     * Response message
     */
    private String message;

    /**
     * Response data payload
     */
    private T data;

    /**
     * Timestamp of the response
     */
    private LocalDateTime timestamp;

    /**
     * Static factory method for success response
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("Operation successful")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Static factory method for success response with custom message
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Static factory method for error response
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }
}

