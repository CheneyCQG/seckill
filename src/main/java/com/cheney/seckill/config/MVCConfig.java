package com.cheney.seckill.config;

import com.cheney.seckill.interceptor.AccessLimitInterceptor;
import com.cheney.seckill.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Component
public class MVCConfig implements WebMvcConfigurer {
    @Autowired
    private UserArgumentResolver userArgumentResolver;
    @Autowired
    private AccessLimitInterceptor accessLimitInterceptor;
    /**
     * SpringMVC 提供的Controller的参数解析器
     * @param resolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        boolean add = resolvers.add(userArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessLimitInterceptor);
    }
}
