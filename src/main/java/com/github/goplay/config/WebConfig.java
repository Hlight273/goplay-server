package com.github.goplay.config;

import com.github.goplay.interceptor.UserInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir.image.playlist-cover}")
    public String playlistCoverDir;
    @Value("${file.upload-dir.image.user-avatar}")
    public String userAvatarDir;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //用户拦截器，除了以下三个白名单，都应该被拦截
        registry.addInterceptor(new UserInterceptor())
                .excludePathPatterns("/user/login", "/user/register","ws")
                .excludePathPatterns("/api/user/login", "/api/user/register","/api/ws")
                .excludePathPatterns("/static/**")
                .excludePathPatterns("/api/static/**")
                .addPathPatterns("/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedOriginPatterns("*")
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")
                .allowedHeaders("*");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 静态资源访问路径映射，详见yml配置
        registry.addResourceHandler("/static/image/playlist-cover/**")
                .addResourceLocations("file:"+playlistCoverDir)  ;
        registry.addResourceHandler("/static/image/user-avatar/**")
                .addResourceLocations("file:"+userAvatarDir)  ;
    }

}
