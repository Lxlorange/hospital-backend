package com.itmk.config.securityConfig;

import com.itmk.netSystem.operationLog.entity.OperationLog;
import com.itmk.netSystem.operationLog.service.OperationLogService;
import com.itmk.netSystem.userWeb.entity.SysUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

public class OperationLogFilter extends OncePerRequestFilter {
    private final OperationLogService operationLogService;

    public OperationLogFilter(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } finally {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof SysUser) {
                SysUser user = (SysUser) authentication.getPrincipal();
                OperationLog log = new OperationLog();
                log.setUserId(user.getUserId());
                log.setUsername(user.getUsername());
                log.setNickName(user.getNickName());
                String ip = request.getHeader("X-Forwarded-For");
                if (StringUtils.isEmpty(ip)) {
                    ip = request.getRemoteAddr();
                }
                log.setIpAddr(ip);
                log.setMethod(request.getMethod());
                log.setUri(request.getRequestURI());
                log.setStatus(response.getStatus());
                log.setOperateTime(new Date());
                operationLogService.save(log);
            }
        }
    }
}