package com.itmk.config.webSecurity.handler;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.itmk.utils.ResultVo;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

// 自定义无权限访问处理器 (处理已认证用户但权限不足的异常)
@Component("customAccessDeineHandler")
public class CustomAccessHandler implements AccessDeniedHandler {
    /**
     * 记录权限不足的事件日志
     * @param request   HTTP请求
     * @param exception 异常信息
     */
    private void logAccessDenied(HttpServletRequest request, AccessDeniedException exception) {
        String username = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "Anonymous";
        String uri = request.getRequestURI();
        // 在实际应用中使用日志框架
        System.err.println("权限拒绝 - 用户: " + username + ", 访问: " + uri + ", 原因: " + exception.getMessage());
    }

    /**
     * 构建无权限的 ResultVo 对象
     * @param message 错误信息
     * @return ResultVo
     */
    private ResultVo buildErrorResponse(String message) {
        return new ResultVo(message, 700, null);
    }

    /**
     * 将Java对象序列化为JSON字符串
     * @param object 要序列化的对象
     * @return JSON字符串
     */
    private String convertObjectToJson(Object object) {
        return JSONObject.toJSONString(object, SerializerFeature.DisableCircularReferenceDetect);
    }

    /**
     * 设置并写入HTTP响应
     * @param response  HTTP响应
     * @param jsonResponse JSON字符串
     * @throws IOException
     */
    private void writeResponse(HttpServletResponse response, String jsonResponse) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream out = response.getOutputStream();
        out.write(jsonResponse.getBytes("UTF-8"));
        out.flush();
        out.close();
    }

    /**
     * 判断请求是否为AJAX请求
     * @param request HTTP请求
     * @return boolean
     */
    private boolean isAjaxRequest(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }


    /**
     * 当用户已认证但缺乏访问资源所需的权限时调用
     */
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException {
        // 构造无权限访问的JSON返回结果，状态码 700
        String res =  JSONObject.toJSONString(new ResultVo("无权限访问,请联系管理员!",700,null), SerializerFeature.DisableCircularReferenceDetect);

        // 设置HTTP响应格式
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        ServletOutputStream out = httpServletResponse.getOutputStream();

        // 写入并发送响应
        out.write(res.getBytes("UTF-8"));
        out.flush();
        out.close();
    }
}