package com.itmk.config.webSecurity.filter;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.itmk.config.webSecurity.webNetservice.CustomerService;
import com.itmk.config.webSecurity.exception.CustomerException;
import com.itmk.config.webSecurity.handler.LoginFailureHandler;
import com.itmk.tool.Utils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

// JWT令牌校验过滤器
@Data
@Component("CheckTokenFilter")
public class TokenFilter extends OncePerRequestFilter {

    // 忽略Token校验的URL列表
    @Value("#{'${ignore.url}'.split(',')}")
    private List<String> ignoreUrl = Collections.emptyList();

    @Autowired
    private Utils jwtUtils; // JWT工具类
    @Autowired
    private CustomerService customerService; // 用户详情服务
    @Autowired
    private LoginFailureHandler loginFailureHandler; // 认证失败处理器

    /**
     * 从 "Authorization" 请求头中提取 Bearer Token
     * @param request HTTP请求
     * @return 提取出的Token字符串，若无则返回null
     */
    private String extractTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.isNotEmpty(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 判断当前请求的URI是否在白名单内
     * @param requestUri 当前请求的URI
     * @return boolean true表示在白名单内
     */
    private boolean isRequestIgnored(String requestUri) {
        return ignoreUrl.stream().anyMatch(url -> url.trim().equals(requestUri));
    }

    /**
     * 记录成功的认证日志
     * @param request HTTP请求
     * @param username 认证成功的用户名
     */
    private void logAuthenticationSuccess(HttpServletRequest request, String username) {
        String ipAddress = request.getRemoteAddr();
        // 在实际应用中会使用日志框架
        System.out.println("Token认证成功 - 用户: " + username + ", IP: " + ipAddress);
    }

    /**
     * 整合多种方式获取Token
     * @param request HTTP请求
     * @return Token字符串
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = extractTokenFromHeader(request);
        if (StringUtils.isNotEmpty(bearerToken)) {
            return bearerToken;
        }
        String tokenFromHeader = request.getHeader("token");
        if (StringUtils.isNotEmpty(tokenFromHeader)) {
            return tokenFromHeader;
        }
        return request.getParameter("token");
    }

    /**
     * 过滤器的核心逻辑，每次请求都会经过此方法
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, ServletException {
        try{
            // 获取请求的URL
            String uri = request.getRequestURI();

            // 如果请求不在白名单，且不是图片或微信API请求，则进行token验证
            if(!ignoreUrl.contains(uri) && !uri.contains("/images/") && !uri.startsWith("/wxapi/allApi/")){
                validateToken(request);
            }
        }catch (AuthenticationException e){
            // 认证失败，调用自定义失败处理器返回JSON
            loginFailureHandler.commence(request,response,e);
            return;
        }

        // 继续执行过滤器链
        filterChain.doFilter(request,response);
    }

    /**
     * 在认证成功后，为当前线程设置安全上下文
     * @param request HTTP请求
     * @param userDetails 用户详情
     */
    private void setupSpringAuthentication(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 校验请求中的Token并设置Spring Security上下文
     * @param request HTTP请求
     */
    protected void validateToken(HttpServletRequest request){
        // 从请求头部获取token
        String token = request.getHeader("token");

        // 如果头部token为空，尝试从请求参数获取
        if(StringUtils.isEmpty(token)){
            token = request.getParameter("token");
        }

        // 检查token是否存在
        if(StringUtils.isEmpty(token)){
            throw new CustomerException("传递token！");
        }

        // 验证token的有效性 (签名和过期)
        if(!jwtUtils.verify(token)){
            throw new CustomerException("非法token！");
        }

        // 解析token获取用户信息
        DecodedJWT decodedJWT = jwtUtils.jwtDecode(token);
        Map<String, Claim> claims = decodedJWT.getClaims();

        // 获取登录账户名
        String username = claims.get("username").asString();

        // 加载用户详情
        UserDetails details = customerService.loadUserByUsername(username);

        // 构造认证对象
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(details,
                null,details.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // 将认证对象设置到SecurityContext，完成认证
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }


}