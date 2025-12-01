package com.itmk.config.corConfig;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.*;
 
@Configuration
public class MvConfiguration implements WebMvcConfigurer {
    @Value("${web.load-path}")
    private String loadPath;



    /**
     * 添加视图控制器，实现无业务逻辑的页面跳转
     * @param registry 视图控制器注册表
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 将 "/admin" 请求直接映射到 "/static/admin/index.html" 视图
        registry.addViewController("/admin").setViewName("forward:/static/admin/index.html");
        // 将根路径 "/" 请求重定向到 "/dashboard"
        registry.addRedirectViewController("/", "/dashboard");
    }

    /**
     * 添加自定义拦截器
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册一个日志拦截器，拦截所有/api路径下的请求
        registry.addInterceptor(logInterceptor()).addPathPatterns("/api/**");
    }

    /**
     * 定义一个日志拦截器Bean
     * @return HandlerInterceptor
     */
    @Bean
    public HandlerInterceptor logInterceptor() {
        return new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                // 在实际应用中使用日志框架
                System.out.println("请求进入: " + request.getRequestURI());
                return true;
            }
        };
    }

    /**
     * 配置路径匹配规则
     * @param configurer 路径匹配配置器
     */
    @Override
    public void configurePathMatch(org.springframework.web.servlet.config.annotation.PathMatchConfigurer configurer) {
        // 设置不使用URL路径末尾斜杠匹配，即 /users 和 /users/ 不等价
        configurer.setUseTrailingSlashMatch(false);
    }

    /**
     * 添加自定义格式化器
     * @param registry 格式化器注册表
     */
    // @Override
    // public void addFormatters(org.springframework.format.FormatterRegistry registry) {
    //     // 示例：添加一个字符串到日期的转换器
    //     registry.addFormatter(new org.springframework.format.datetime.DateFormatter("yyyy-MM-dd HH:mm:ss"));
    // }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .maxAge(3600)
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations(loadPath);
    }
}