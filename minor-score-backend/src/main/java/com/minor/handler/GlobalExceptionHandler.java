package com.minor.handler;

import com.minor.vo.Result;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 拦截所有Controller抛出的异常，统一返回Result格式的错误响应，
 * 避免前端收到原始的500错误或堆栈信息
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常（RuntimeException）
     * 包括：用户名密码错误、数据不存在、参数校验失败等
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<String> handleRuntimeException(RuntimeException e) {
        return Result.error(e.getMessage());
    }

    /**
     * 处理认证失败异常
     * Spring Security抛出的BadCredentialsException
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<String> handleBadCredentialsException(BadCredentialsException e) {
        return Result.error("用户名或密码错误");
    }

    /**
     * 处理权限不足异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<String> handleAccessDeniedException(AccessDeniedException e) {
        return Result.error(403, "权限不足，无法访问该资源");
    }

    /**
     * 处理所有未捕获的异常
     * 作为兜底处理器，防止堆栈信息泄露到前端
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<String> handleException(Exception e) {
        // 记录日志（实际项目中应使用SLF4J）
        e.printStackTrace();
        return Result.error("服务器内部错误：" + e.getMessage());
    }
}
