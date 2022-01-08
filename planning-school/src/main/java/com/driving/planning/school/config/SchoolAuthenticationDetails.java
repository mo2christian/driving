package com.driving.planning.school.config;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

public class SchoolAuthenticationDetails extends WebAuthenticationDetails {

    private final String school;

    public SchoolAuthenticationDetails(HttpServletRequest request) {
        super(request);
        school = request.getParameter("school");
    }

    public String getSchool() {
        return school;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SchoolAuthenticationDetails that = (SchoolAuthenticationDetails) o;
        return Objects.equals(school, that.school);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), school);
    }
}
