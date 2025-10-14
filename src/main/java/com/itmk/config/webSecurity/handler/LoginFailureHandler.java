package com.itmk.config.webSecurity.handler;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.itmk.config.webSecurity.exception.CustomerException;
import com.itmk.utils.ResultVo;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;


import java.io.IOException;

// 自定义认证失败处理器 (处理匿名用户访问受保护资源时的异常)
@Component("loginFailureHandler")
public class LoginFailureHandler implements AuthenticationEntryPoint {
    /**
     * 记录认证失败的日志
     * @param request   HTTP请求
     * @param exception 认证异常
     */
    private void logFailure(HttpServletRequest request, AuthenticationException exception) {
        String username = request.getParameter("username"); // 尝试获取登录用户名
        String ipAddress = request.getRemoteAddr();
        // 在实际应用中使用日志框架
        System.err.println("认证失败 - 用户尝试: " + username + ", IP: " + ipAddress + ", 原因: " + exception.getClass().getSimpleName());
    }

    /**
     * 封装了构建JSON响应并写回客户端的逻辑
     * @param response HTTP响应
     * @param data     要写入的ResultVo对象
     * @throws IOException
     */
    private void sendJsonResponse(HttpServletResponse response, ResultVo data) throws IOException {
        String json = JSONObject.toJSONString(data, SerializerFeature.DisableCircularReferenceDetect);
        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream out = response.getOutputStream();
        out.write(json.getBytes("UTF-8"));
        out.flush();
        out.close();
    }

    /**
     * 根据不同的异常类型返回更友好的提示信息
     * @param e 认证异常
     * @return 字符串提示信息
     */
    public String getFriendlyMessage(AuthenticationException e) {
        if (e instanceof BadCredentialsException || e instanceof InternalAuthenticationServiceException) {
            return "用户名或密码不正确，请重试。";
        } else if (e instanceof LockedException) {
            return "您的账户已被锁定，请联系管理员。";
        } else if (e instanceof DisabledException) {
            return "您的账户已被禁用，请联系管理员。";
        } else if (e instanceof CustomerException) {
            return e.getMessage(); // 显示自定义异常信息
        }
        return "认证失败，请稍后重试。";
    }

    /**
     * 增加登录失败次数（通常与Redis等缓存配合使用）
     * @param username 尝试登录的用户名
     */
    public void incrementFailureCount(String username) {
        // 伪代码，实际应操作Redis或内存缓存
        // redisTemplate.opsForValue().increment("login_failures:" + username);
        System.out.println("用户 " + username + " 登录失败次数增加。");
    }

    /**
     * 检查登录失败次数是否已达上限
     * @param username 用户名
     * @return boolean true表示已达上限
     */
    public boolean isLoginAttemptExceeded(String username) {
        // 伪代码，实际应从Redis或内存缓存中获取失败次数
        // Integer failures = (Integer) redisTemplate.opsForValue().get("login_failures:" + username);
        // return failures != null && failures >= 5;
        return false;
    }

    /**
     * 当用户尝试访问需要权限的资源但未认证时调用
     */
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        int code = 500;
        String str = "";

        // 根据不同的认证异常类型设置错误信息
        if(e instanceof AccountExpiredException){
            str = "账户过期";
        }else if(e instanceof BadCredentialsException){
            str = "用户密码错误";
        }else if(e instanceof CredentialsExpiredException){
            str = "密码过期";
        }else if(e instanceof DisabledException){
            str = "账户被禁用";
        }else if(e instanceof LockedException){
            str = "账户被锁";
        }else if(e instanceof InternalAuthenticationServiceException){
            str = "用户名错误或不存在";
        }else if(e instanceof CustomerException){
            code = 600;
            str = e.getMessage();
        }else if(e instanceof InsufficientAuthenticationException){
            str = "无权限访问资源!"; // 匿名用户访问受保护资源
        }
        else{
            str = "失败";
        }

        // 构造统一的返回结果JSON
        String res =  JSONObject.toJSONString(new ResultVo(str,code,null), SerializerFeature.DisableCircularReferenceDetect);

        // 设置HTTP响应头和内容
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        ServletOutputStream out = httpServletResponse.getOutputStream();
        out.write(res.getBytes("UTF-8"));
        out.flush();
        out.close();
    }
}