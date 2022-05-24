package com.driving.planning.school.config;

import com.driving.planning.client.AccountApiClient;
import com.driving.planning.client.model.AccountDto;
import com.driving.planning.school.common.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.ArrayList;

@Slf4j
public class SchoolAuthenticationProvider implements AuthenticationProvider {

    private final AccountApiClient accountApiClient;

    public SchoolAuthenticationProvider(AccountApiClient accountApiClient) {
        this.accountApiClient = accountApiClient;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("Authenticate user {}", authentication.getPrincipal());
        var details = (SchoolAuthenticationDetails)authentication.getDetails();
        var account = new AccountDto()
                .email((String)authentication.getPrincipal())
                .password((String)authentication.getCredentials());
        try{
            accountApiClient.isValidAccount(details.getSchool(), account);
        }
        catch(ApiException ex){
            log.debug("Error while checking account", ex);
            throw new BadCredentialsException("Invalid account");
        }
        return new UsernamePasswordAuthenticationToken(account.getEmail(), account.getPassword(), new ArrayList<>());
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
    }
}
