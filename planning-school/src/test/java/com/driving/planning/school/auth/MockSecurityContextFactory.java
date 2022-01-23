package com.driving.planning.school.auth;

import com.driving.planning.school.config.SchoolAuthenticationDetails;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class MockSecurityContextFactory implements WithSecurityContextFactory<MockUser> {

    @Override
    public SecurityContext createSecurityContext(MockUser mockUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        UserDetails user = new User(mockUser.username(), "", AuthorityUtils.createAuthorityList());
        var authentication = new UsernamePasswordAuthenticationToken(
                user, user.getPassword(), user.getAuthorities());
        var request = new MockHttpServletRequest();
        request.setParameter("school", mockUser.school());
        authentication.setDetails(new SchoolAuthenticationDetails(request));
        context.setAuthentication(authentication);
        return context;
    }

}
