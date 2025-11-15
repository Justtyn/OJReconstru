package com.oj.onlinejudge.exception;

// 自定义业务异常：携带错误码，便于全局处理

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final ApiErrorCode errorCode;

    public ApiException(ApiErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    public ApiException(ApiErrorCode errorCode, String message) {
        super(message == null ? errorCode.getDefaultMessage() : message);
        this.errorCode = errorCode;
    }

    public static ApiException badRequest(String message) {
        return new ApiException(ApiErrorCode.BAD_REQUEST, message);
    }

    public static ApiException unauthorized(String message) {
        return new ApiException(ApiErrorCode.UNAUTHORIZED, message);
    }

    public static ApiException forbidden(String message) {
        return new ApiException(ApiErrorCode.FORBIDDEN, message);
    }

    public static ApiException notFound(String message) {
        return new ApiException(ApiErrorCode.NOT_FOUND, message);
    }

    public static ApiException conflict(String message) {
        return new ApiException(ApiErrorCode.CONFLICT, message);
    }

    public static ApiException internal(String message) {
        return new ApiException(ApiErrorCode.INTERNAL_ERROR, message);
    }
}
