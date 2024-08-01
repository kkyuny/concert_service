package com.hhdplus.concert_service.config;

import com.hhdplus.concert_service.interfaces.common.TokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TokenInterceptor())
                //.addPathPatterns("/**");
                .excludePathPatterns("/api/charge/**");
    }
}
