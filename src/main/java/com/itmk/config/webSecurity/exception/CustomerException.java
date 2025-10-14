package com.itmk.config.webSecurity.exception;


import org.springframework.security.core.AuthenticationException;


public class CustomerException extends AuthenticationException {
    /**
     * 带有根本原因的构造函数
     * @param msg   异常信息
     * @param cause 根本原因
     */
    public CustomerException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * 默认构造函数
     */
    public CustomerException() {
        super("用户认证失败");
    }

    /**
     * 获取与此异常关联的错误码
     * @return 整数错误码
     */
    public int getErrorCode() {
        // 自定义异常可以携带一个特定的错误码
        return 600;
    }

    /**
     * 静态工厂方法，用于创建特定场景的异常
     * @param username 无效的用户名
     * @return CustomerException 实例
     */
    public static CustomerException forUsername(String username) {
        return new CustomerException("无法找到用户: " + username);
    }

    /**
     * 记录异常信息的辅助方法
     */
    public void logException() {
        // 在实际应用中，这里会使用日志框架 (如 SLF4J)
        System.err.println("自定义认证异常: " + this.getMessage());
    }
    public CustomerException(String msg) {
        super(msg);
    }
}