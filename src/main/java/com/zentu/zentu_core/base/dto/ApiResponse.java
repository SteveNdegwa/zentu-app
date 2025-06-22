package com.zentu.zentu_core.base.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    private String code;
    private String message;
    private Object data;

    public static ResponseEntity<ApiResponse> ok(String message, Object data) {
        return ResponseEntity.ok(new ApiResponse("200.000.000", message, data));
    }

    public static ResponseEntity<ApiResponse> created(String message, Object data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("201.000.000", message, data));
    }

    public static ResponseEntity<ApiResponse> badRequest(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse("400.000.000", message, null));
    }

    public static ResponseEntity<ApiResponse> unauthorized(String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse("401.000.000", message, null));
    }

    public static ResponseEntity<ApiResponse> forbidden(String message) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse("403.000.000", message, null));
    }

    public static ResponseEntity<ApiResponse> notFound(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse("404.000.000", message, null));
    }

    public static ResponseEntity<ApiResponse> internalError(String message) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("500.000.000", message, null));
    }
}
