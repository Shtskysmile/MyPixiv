package org.example.PCOI.Config;

import org.example.PCOI.Utils.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${pcoi.upload.base-dir}")
    private String uploadBaseDir;
    @Value("${pcoi.upload.url-prefix:/files/}")
    private String uploadUrlPrefix;

    private final RequestLoggingInterceptor requestLoggingInterceptor;
    private final JwtInterceptor jwtInterceptor = new JwtInterceptor();
    private final JwtUserInterceptor jwtUserInterceptor = new JwtUserInterceptor();
    private final JwtSysAdminInterceptor jwtSysAdminInterceptor = new JwtSysAdminInterceptor();
    private final JwtCommunityAdminInterceptor jwtCommunityAdminInterceptor = new JwtCommunityAdminInterceptor();
    public WebConfig(RequestLoggingInterceptor requestLoggingInterceptor) {
        this.requestLoggingInterceptor = requestLoggingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 统一日志拦截
        registry.addInterceptor(requestLoggingInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                    "/login",
                    "/register",
                    "/mySecurityIssues",
                    "/verifySecurityIssue",
                    "/updatePassword",
                    "/illustrations",
                    "/mangas",
                    "/contribution",
                    "/search",
                    "/userInfo",
                    // 静态资源
                    "/files/**",
                    "/img/**",
                    "/allContributions"
                );

        // 所有接口通用拦截器
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/login",
                        "/register",
                        "/mySecurityIssues",
                        "/verifySecurityIssue",
                        "/updatePassword",
                        "/illustrations",
                        "/mangas",
                        "/contribution",
                        "/search",
                        "/userInfo",
                        // 静态资源
                        "/files/**",
                        "/img/**",
                        "/allContributions"
                );

        // 普通用户接口
        registry.addInterceptor(jwtUserInterceptor)
                .addPathPatterns("/user/**");

        // 系统管理员接口
        registry.addInterceptor(jwtSysAdminInterceptor)
                .addPathPatterns("/systemAdmin/**");

        // 社区管理员接口
        registry.addInterceptor(jwtCommunityAdminInterceptor)
                .addPathPatterns("/communityAdmin/**");
    }

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // 统一将 /files/** 映射到本地上传目录，供前端直接访问
        String prefix = uploadUrlPrefix;
        if (prefix == null || prefix.isBlank()) prefix = "/files/";
        if (!prefix.startsWith("/")) prefix = "/" + prefix;
        if (!prefix.endsWith("/")) prefix = prefix + "/";
        String base = uploadBaseDir == null ? "uploads/" : uploadBaseDir;
        base = base.replace('\\', '/');
        if (!base.endsWith("/")) base = base + "/";
        registry.addResourceHandler(prefix + "**")
                .addResourceLocations("file:" + base);
    }
}