package com.driving.planning.school.dto;


import com.driving.planning.common.constraint.PhoneNumber;
import com.driving.planning.common.hourly.Hourly;
import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@RegisterForReflection
public class SchoolDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String pseudo;

    @NotBlank
    private String name;

    @PhoneNumber
    private String phoneNumber;

    @NotNull
    @Valid
    private AddressDto address;

    private Set<Hourly> workDays;

    public SchoolDto() {
        workDays = new HashSet<>();
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Set<Hourly> getWorkDays() {
        return workDays;
    }

    public void setWorkDays(Set<Hourly> workDays) {
        this.workDays = workDays;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }
}
