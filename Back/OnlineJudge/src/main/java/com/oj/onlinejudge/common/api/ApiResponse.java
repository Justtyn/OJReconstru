package com.oj.onlinejudge.common.api;

// 统一API响应包装：包含状态码、消息与数据

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /**
     * 业务状态码，0 表示成功
     */
    private int code;

    /**
     * 人类可读的提示消息
     */
    private String message;

    /**
     * 泛型数据载体
     */
    private T data;

    /**
     * 成功响应（默认消息）
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(0, "success", data);
    }

    /**
     * 成功响应（自定义消息）
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(0, message, data);
    }

    /**
     * 失败响应
     */
    public static <T> ApiResponse<T> failure(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}