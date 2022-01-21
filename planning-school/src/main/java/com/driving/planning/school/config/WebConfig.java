package com.driving.planning.school.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        WebMvcConfigurer.super.addResourceHandlers(registry);
        registry
                .addResourceHandler("/public/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod( 3600 )
                .resourceChain(true) // 4.1
                .addResolver(new PathResourceResolver()); //4.1
    }
}
