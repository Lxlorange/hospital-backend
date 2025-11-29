package com.itmk.config.webSecurity;

import com.itmk.config.webSecurity.webNetservice.CustomerService;
import com.itmk.config.webSecurity.filter.TokenFilter;
import com.itmk.config.webSecurity.handler.CustomAccessHandler;
import com.itmk.config.webSecurity.handler.LoginFailureHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.Collections;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

// Spring Security配置类
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // 启用方法级别的安全注解
public class SpringSecurityConfig {
    @Autowired
    private CustomerService customerService; // 自定义用户查询服务
    @Autowired
    private LoginFailureHandler loginFailureHandler; // 认证失败处理器 (匿名用户访问受保护资源)
    @Autowired
    private CustomAccessHandler customAccessHandler; // 权限不足处理器
    @Autowired
    private TokenFilter tokenFilter; // JWT令牌校验过滤器


    /**
     * 密码加密器配置
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



    /**
     * 跨域资源共享(CORS)配置 (统一配置源)
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 1. 允许任何来源 (这里使用 setAllowedOriginPatterns 支持带凭证的通配符)
        //    比 setAllowedOrigins("*") 更安全、更推荐
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        // 2. 允许任何请求头
        configuration.addAllowedHeader(CorsConfiguration.ALL);
        // 3. 允许任何HTTP方法 (GET, POST, etc)
        configuration.addAllowedMethod(CorsConfiguration.ALL);
        // 4. 允许浏览器发送凭证 (如 Cookies, Authorization Header)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有路径 ("/**") 应用上面的CORS配置
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 静态资源白名单
     * @return 静态资源路径数组
     */
    private String[] getStaticResources() {
        return new String[]{
                "/css/**",
                "/js/**",
                "/images/**",
                "/favicon.ico"
        };
    }

    /**
     * API文档白名单 (如Swagger)
     * @return API文档路径数组
     */
    private String[] getSwaggerPermitUrls() {
        return new String[]{
                "/swagger-ui.html",
                "/swagger-resources/**",
                "/v3/api-docs/**",
                "/webjars/**"
        };
    }

    /**
     * Actuator健康检查端点白名单
     * @return Actuator路径数组
     */
    private String[] getActuatorPermitUrls() {
        return new String[]{
                "/actuator/**"
        };
    }


    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * 配置安全过滤链
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 配置JWT令牌校验过滤器，在 UsernamePasswordAuthenticationFilter 之前执行
        http.addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class)
                // 禁用CSRF和CORS (跨域处理)
                .csrf(AbstractHttpConfigurer::disable)

                // 启用CORS，使用我们上面定义的 corsConfigurationSource Bean
                .cors(Customizer.withDefaults())
                // 允许Iframe嵌套
                .headers((headers) -> headers.frameOptions((HeadersConfigurer.FrameOptionsConfig::disable)))

                // 设置为无状态session管理
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 鉴权配置
                .authorizeHttpRequests((authorized ) ->authorized
                                // 配置不需要认证即可访问的白名单接口
                                .requestMatchers(
                                        "/api/sysUser/getImage",
                                        "/api/sysUser/login",
                                        "/api/upload/uploadImage",
                                        "/images/**",
                                        "/api/images/**",
                                        "/wxapi/allApi/**",
                                        "/wxapi/allApi/reapply",
                                        "/wxapi/allapi/**",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/swagger-resources/**",
                                        "/v3/api-docs",
                                        "/v2/api-docs",
                                        "/webjars/**",
                                        "/api/sysUser/api-docs",
                                        "/webjars",
                                        "/doc.html",
                                        "/favicon.ico",
                                        "/api/statistic/**",
                                        "/api/LLM/**",
                                        "/captcha/**"
                                ).permitAll()
                        // 其他所有请求都需要认证
                        .anyRequest().authenticated()
                )

                // 指定查询用户信息的实现类
                .userDetailsService(customerService)
                // 自定义异常处理
                .exceptionHandling((exceptionHandling) -> exceptionHandling
                        .authenticationEntryPoint(loginFailureHandler) // 匿名用户访问无权限
                        .accessDeniedHandler(customAccessHandler)  // 已认证用户但无权限
                );
        // 构建并返回SecurityFilterChain
        return http.build();
    }
    }