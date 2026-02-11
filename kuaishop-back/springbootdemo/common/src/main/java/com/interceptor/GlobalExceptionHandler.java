package com.interceptor;


import generator.domain.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获所有异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(500, "系统内部错误");
    }

    /**
     * 捕获参数错误异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("参数错误", e);
        return Result.error(400, e + "参数错误");
    }

    /**
     * 处理Spring Validation注解的校验异常（如@Pattern、@NotBlank）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 返回400状态码（参数错误）
    public Result<Object> handleValidationException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        // 获取所有校验失败的字段信息
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        if (!fieldErrors.isEmpty()) {
            // 取第一个错误提示（也可以拼接所有错误，根据需求调整）
            FieldError firstError = fieldErrors.get(0);
            String errorMsg = String.format("[%s] %s", firstError.getField(), firstError.getDefaultMessage());
            log.error("参数校验失败：{}", errorMsg);
            // 返回自定义的Result格式（和你业务一致）
            return Result.error(400, errorMsg);
        }

        // 兜底提示
        return Result.error(400, "请求参数格式错误");
    }

    /**
     * 捕获Redis连接异常
     */
    @ExceptionHandler(RedisConnectionFailureException.class)
    public ResponseEntity<ErrorResponse> handleRedisException(RedisConnectionFailureException e) {
        log.error("Redis连接异常：", e);
        ErrorResponse error = new ErrorResponse(500, "Redis服务异常：" + e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 统一的错误响应体
    public static class ErrorResponse {
        private int code;
        private String message;

        public ErrorResponse(int code, String message) {
            this.code = code;
            this.message = message;
        }

        // getter/setter
        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
