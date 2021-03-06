package com.driving.planning.school;

import com.driving.planning.school.config.FeignConfig;
import com.driving.planning.school.config.SecurityConfig;
import com.driving.planning.school.config.WebConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({FeignConfig.class, SecurityConfig.class, WebConfig.class})
public class PlanningWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlanningWebApplication.class, args);
	}

}
