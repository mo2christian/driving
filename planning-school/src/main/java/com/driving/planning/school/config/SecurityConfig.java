package com.driving.planning.school.config;

import com.driving.planning.client.AccountApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AccountApiClient accountApiClient;

    @Autowired
    public SecurityConfig(AccountApiClient accountApiClient) {
        this.accountApiClient = accountApiClient;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers(HttpMethod.POST, "/register").permitAll()
                    .antMatchers(HttpMethod.GET, "/register").permitAll()
                    .antMatchers("/public/**").permitAll()
                    .anyRequest().authenticated()
                .and()
                    .formLogin()
                    .loginPage("/login")
                    .authenticationDetailsSource(new SchoolAuthenticationDetailSource())
                    .permitAll()
                .and()
                    .logout()
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .permitAll()
                .and()
                    .csrf();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(new SchoolAuthenticationProvider(accountApiClient));
    }
}

