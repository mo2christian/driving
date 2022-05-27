package com.driving.planning.school.config;

import com.driving.planning.school.common.converter.String2LocalDate;
import com.driving.planning.school.common.converter.String2LocalTime;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
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
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        WebMvcConfigurer.super.addFormatters(registry);
        registry.addConverter(new String2LocalTime());
        registry.addConverter(new String2LocalDate());
    }
}
