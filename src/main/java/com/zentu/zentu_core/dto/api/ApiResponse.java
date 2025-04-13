package com.zentu.zentu_core.dto.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private String code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>("100.000.000", message, data);
    }

    public static ApiResponse<Object> error(String errorCode, String message) {
        return new ApiResponse<>(errorCode, message, null);
    }
}
