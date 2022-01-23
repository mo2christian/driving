package com.driving.planning.school.auth;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@WithSecurityContext(factory=MockSecurityContextFactory.class)
public @interface MockUser {

    String username();

    String school() default "school";

}
