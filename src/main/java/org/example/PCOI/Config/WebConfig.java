package org.example.PCOI.Config;

import org.example.PCOI.Utils.JwtInterceptor;
import org.example.PCOI.Utils.JwtUserInterceptor;
import org.example.PCOI.Utils.JwtSysAdminInterceptor;
import org.example.PCOI.Utils.JwtCommunityAdminInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final RequestLoggingInterceptor requestLoggingInterceptor;

    public WebConfig(RequestLoggingInterceptor requestLoggingInterceptor) {
        this.requestLoggingInterceptor = requestLoggingInterceptor;
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 统一日志拦截，建议第一个注册
        registry.addInterceptor(requestLoggingInterceptor)
                .addPathPatterns("/**");
        // 所有接口通用拦截器
        registry.addInterceptor(new JwtInterceptor())
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
                        "/userInfo");

        // 普通用户接口
        registry.addInterceptor(new JwtUserInterceptor())
                .addPathPatterns("/user/**");

        // 系统管理员接口
        registry.addInterceptor(new JwtSysAdminInterceptor())
                .addPathPatterns("/systemAdmin/**");

        // 社区管理员接口
        registry.addInterceptor(new JwtCommunityAdminInterceptor())
                .addPathPatterns("/communityAdmin/**");
    }
}