package com.oj.onlinejudge.exception;

// 全局异常处理：统一转换为 ApiResponse，便于前端处理

import com.oj.onlinejudge.common.api.ApiResponse;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException ex) {
        ApiErrorCode code = ex.getErrorCode();
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.failure(code.getCode(), ex.getMessage()));
    }

    /**
     * 处理参数校验异常，返回 400
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidationExceptions(Exception ex) {
        String errorMessage = "";
        if (ex instanceof MethodArgumentNotValidException) {
            errorMessage = ((MethodArgumentNotValidException) ex).getBindingResult()
                    .getFieldErrors().stream()
                    .map(err -> err.getField() + ":" + err.getDefaultMessage())
                    .collect(Collectors.joining("; "));
        } else if (ex instanceof BindException) {
            errorMessage = ((BindException) ex).getBindingResult().getAllErrors().stream()
                    .map(err -> err.getDefaultMessage())
                    .collect(Collectors.joining("; "));
        }
        return ApiResponse.failure(HttpStatus.BAD_REQUEST.value(), errorMessage);
    }

    /**
     * 处理常见的参数错误，返回 400
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleIllegalArgument(IllegalArgumentException ex) {
        return ApiResponse.failure(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<Void> handleDuplicateKey(DuplicateKeyException ex) {
        return ApiResponse.failure(HttpStatus.CONFLICT.value(), "数据已存在或违反唯一约束");
    }

    /**
     * 兜底异常处理，返回 500
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleUnknownException(Exception ex) {
        log.error("Unexpected error", ex);
        return ApiResponse.failure(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误");
    }
}
