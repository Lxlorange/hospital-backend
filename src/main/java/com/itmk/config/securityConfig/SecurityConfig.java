package com.itmk.config.securityConfig;

import com.itmk.netSystem.menuWebNet.entity.SysMenu;
import com.itmk.netSystem.menuWebNet.service.menuWebNetService;
import com.itmk.netSystem.userWeb.entity.SysUser;
import com.itmk.netSystem.userWeb.service.userWebService;
import com.itmk.tool.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final Utils jwtUtils;
    private final userWebService userService;
    private final menuWebNetService menuService;

    @Value("${ignore.url:}")
    private String ignoreUrl;

    public SecurityConfig(Utils jwtUtils, userWebService userService, menuWebNetService menuService) {
        this.jwtUtils = jwtUtils;
        this.userService = userService;
        this.menuService = menuService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            SysUser user = userService.loadUser(username);
            if (user == null) {
                throw new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found");
            }
            List<SysMenu> menus = menuService.getMenuByUserId(user.getUserId());
            List<SimpleGrantedAuthority> authorities = Optional.ofNullable(menus).orElse(List.of())
                    .stream()
                    .filter(m -> m != null && m.getCode() != null && !m.getCode().isEmpty())
                    .flatMap(m -> java.util.Arrays.stream(m.getCode().split(",")))
                    .filter(s -> s != null && !s.isEmpty())
                    .map(SimpleGrantedAuthority::new)
                    .distinct()
                    .collect(Collectors.toList());
            user.setAuthorities(authorities);
            return user;
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(UserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(jwtUtils, userDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider authenticationProvider, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        String[] whitelist = parseWhitelist(ignoreUrl);
        http.csrf(csrf -> csrf.disable());
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(whitelist).permitAll()
                .requestMatchers("/captcha/**").permitAll()
                .requestMatchers("/wxapi/allApi/login").permitAll()
                .requestMatchers("/api/upload/uploadImage").permitAll()
                .requestMatchers("/api/sysUser/getImage").permitAll()
                .requestMatchers("/api/sysUser/login").permitAll()
                .requestMatchers("/webjars/**").permitAll()
                .requestMatchers("/doc.html").permitAll()
                .requestMatchers("/favicon.ico").permitAll()
                .requestMatchers("/images/**").permitAll()
                .requestMatchers("/api/statistic/**").permitAll()
                .requestMatchers("/api/LLM/**").permitAll()
                .anyRequest().authenticated()
        );
        http.authenticationProvider(authenticationProvider);
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(401);
                    response.setContentType("application/json;charset=UTF-8");
                    String body = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(com.itmk.utils.ResultUtils.error("未登录", 401));
                    response.getWriter().write(body);
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(403);
                    response.setContentType("application/json;charset=UTF-8");
                    String body = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(com.itmk.utils.ResultUtils.error("权限不足", 403));
                    response.getWriter().write(body);
                })
        );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    private String[] parseWhitelist(String csv) {
        if (csv == null || csv.isEmpty()) {
            return new String[]{"/api/sysUser/login", "/api/sysUser/getImage","/wxapi/allApi/**"};
        }
        return java.util.Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
    }

    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
