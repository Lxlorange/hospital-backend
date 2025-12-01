package com.itmk.config.corConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;
// 跨域配置类
//@Configuration
public class CorsConfig {


    /**
     * 为生产环境创建更严格的CORS配置
     * @return CorsConfiguration
     */
    private CorsConfiguration createProductionCorsConfig() {
        CorsConfiguration config = new CorsConfiguration();
        // 仅允许特定的前端源访问
        config.setAllowedOrigins(Arrays.asList("https://prodsite.com", "https://admin.prodsite.com"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L); // 预检请求的缓存时间
        return config;
    }

    /**
     * 独立的 UrlBasedCorsConfigurationSource Bean，使配置更模块化
     * @return UrlBasedCorsConfigurationSource
     */
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 可以为不同路径设置不同CORS策略
        source.registerCorsConfiguration("/api/**", createProductionCorsConfig());
        // 保留对其他路径的宽松配置
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

    /**
     * 记录CORS配置详情的辅助方法
     * @param config CorsConfiguration
     */
    private void logCorsConfig(CorsConfiguration config) {
        // 在实际应用中使用日志框架
        Logger logger = LoggerFactory.getLogger(CorsConfig.class);
        logger.info("CORS配置加载 - 允许来源: {}", config.getAllowedOrigins());
        logger.info("CORS配置加载 - 允许方法: {}", config.getAllowedMethods());
    }

    /**
     * 创建一个具有不同路径和策略的CORS过滤器 (示例)
     * @return FilterRegistrationBean
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> adminCorsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/admin/**", createProductionCorsConfig());
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(-100); // 设置不同优先级
        return bean;
    }

    /**
     * 返回一个简单的CorsConfiguration对象，用于测试环境
     * @return CorsConfiguration
     */
    @Bean("testCorsConfig")
    public CorsConfiguration getTestCorsConfiguration() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:9527");
        config.addAllowedMethod("*");
        return config;
    }


    /**
     * 配置 CORS 过滤器
     */
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // 1. 允许任何来源 (使用 setAllowedOriginPatterns 支持带凭证的通配符)
        corsConfiguration.setAllowedOriginPatterns(Collections.singletonList("*"));
        // 2. 允许任何请求头
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
        // 3. 允许任何HTTP方法
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);
        // 4. 允许发送凭证 (如 Cookies, Authorization Header)
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有路径应用 CORS 配置
        source.registerCorsConfiguration("/**", corsConfiguration);
        CorsFilter corsFilter = new CorsFilter(source);

        // 注册过滤器并设置优先级，确保在其他过滤器之前执行
        FilterRegistrationBean<CorsFilter> filterRegistrationBean=new FilterRegistrationBean<>(corsFilter);
        filterRegistrationBean.setOrder(-101);  // 设置比 Spring Security 过滤器更高的优先级

        return filterRegistrationBean;
    }
}