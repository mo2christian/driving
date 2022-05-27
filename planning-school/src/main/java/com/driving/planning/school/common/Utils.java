package com.driving.planning.school.common;

import com.driving.planning.school.config.SchoolAuthenticationDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class Utils {
    private Utils() {
    }

    public static String getSchoolID(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var authenticationToken = (UsernamePasswordAuthenticationToken)authentication;
        var details = (SchoolAuthenticationDetails)authenticationToken.getDetails();
        return details.getSchool();
    }

}
