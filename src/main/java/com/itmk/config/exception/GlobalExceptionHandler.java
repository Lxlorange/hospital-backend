package com.itmk.config.exception;

import com.itmk.utils.ResultUtils;
import com.itmk.utils.ResultVo;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultVo<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ":" + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResultUtils.error(msg, 400);
    }

    @ExceptionHandler(BindException.class)
    public ResultVo<?> handleBindException(BindException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ":" + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResultUtils.error(msg, 400);
    }


    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResultVo<?> handleMissingParam(MissingServletRequestParameterException ex) {
        return ResultUtils.error("缺少参数:" + ex.getParameterName(), 400);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResultVo<?> handleMessageNotReadable(HttpMessageNotReadableException ex) {
        return ResultUtils.error("请求体格式错误", 400);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResultVo<?> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return ResultUtils.error("请求方法不支持", 405);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResultVo<?> handleBadCredentials(BadCredentialsException ex) {
        return ResultUtils.error("用户名或密码错误", 401);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResultVo<?> handleUsernameNotFound(UsernameNotFoundException ex) {
        return ResultUtils.error("用户不存在", 401);
    }

    @ExceptionHandler({DisabledException.class, LockedException.class, AccountStatusException.class})
    public ResultVo<?> handleAccountStatus(Exception ex) {
        return ResultUtils.error("账户状态异常", 403);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResultVo<?> handleAccessDenied(AccessDeniedException ex) {
        return ResultUtils.error("权限不足", 403);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResultVo<?> handleDuplicateKey(DuplicateKeyException ex) {
        return ResultUtils.error("数据重复", 409);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResultVo<?> handleIllegalArgument(IllegalArgumentException ex) {
        return ResultUtils.error(ex.getMessage(), 400);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResultVo<?> handleIllegalState(IllegalStateException ex) {
        return ResultUtils.error(ex.getMessage(), 500);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResultVo<?> handleRuntime(RuntimeException ex) {
        String msg = ex.getMessage();
        if (msg == null || msg.isBlank()) {
            msg = "服务器内部错误";
        }
        return ResultUtils.error(msg, 500);
    }

    @ExceptionHandler(Exception.class)
    public ResultVo<?> handleException(Exception ex) {
        return ResultUtils.error("服务器内部错误", 500);
    }
}
