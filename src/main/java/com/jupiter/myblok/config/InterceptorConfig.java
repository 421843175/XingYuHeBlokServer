package com.jupiter.myblok.config;

import com.jupiter.myblok.interceptor.TokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Token拦截器配置类
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Resource
    private TokenInterceptor tokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        List<String> excludePath = new ArrayList<>();
        excludePath.add("/article/**");    // 登录注册请求不拦截
        excludePath.add("/record/**"); // 获取头像或背景 不拦截
        excludePath.add("/webInfo/**"); // 获取别人的头像或背景 不拦截
        excludePath.add("/admin/login");  //登录 不拦截
        excludePath.add("/error");
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(excludePath);
        //除了登陆接口其他所有接口都需要token验证
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
