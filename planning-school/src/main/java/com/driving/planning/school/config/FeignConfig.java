package com.driving.planning.school.config;

import com.driving.planning.school.common.ApiException;
import feign.Response;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = {"com.driving.planning.client"})
public class FeignConfig {

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(100, 2000, 3);
    }

    @Bean
    public ErrorDecoder errorDecoder(){
        return (s, response) -> new ApiException(response.reason());
    }

}
