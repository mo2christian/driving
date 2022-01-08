package com.driving.planning.school.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.io.Serializable;
import java.util.List;

@RegisterForReflection
public class SchoolResponse implements Serializable {

    private final List<SchoolDto> schools;

    public SchoolResponse(List<SchoolDto> schools) {
        this.schools = schools;
    }

    public List<SchoolDto> getSchools() {
        return schools;
    }

}
