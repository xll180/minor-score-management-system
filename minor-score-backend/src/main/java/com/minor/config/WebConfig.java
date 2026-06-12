package com.minor.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置类
 * 配置CORS跨域映射和静态资源路径
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置CORS跨域映射
     * 允许所有来源访问所有接口
     *
     * @param registry CORS注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")                    // 对所有路径生效
                .allowedOriginPatterns("*")            // 允许所有来源
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") // 允许的HTTP方法
                .allowedHeaders("*")                   // 允许所有请求头
                .allowCredentials(true)               // 允许携带凭证
                .maxAge(3600);                        // 预检请求缓存时间（秒）
    }

    /**
     * 配置静态资源映射
     * 将导出文件目录映射为可访问的静态资源路径
     *
     * @param registry 资源处理器注册器
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将本地./exports/目录映射为对外访问的/exports/**路径
        registry.addResourceHandler("/exports/**")
                .addResourceLocations("file:./exports/");
    }
}