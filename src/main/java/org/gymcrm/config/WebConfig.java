package org.gymcrm.config;

import lombok.RequiredArgsConstructor;
import org.gymcrm.interceptor.AuthenticationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthenticationInterceptor authenticationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new org.gymcrm.interceptor.TransactionInterceptor())
                .addPathPatterns("/**");

        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/api/v1/**")
                .excludePathPatterns(
                        "/api/v1/auth/trainee/register",
                        "/api/v1/auth/trainer/register",
                        "/api/v1/auth/login"
                );
    }
}