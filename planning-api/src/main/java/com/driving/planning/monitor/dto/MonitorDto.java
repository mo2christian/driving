package com.driving.planning.monitor.dto;

import com.driving.planning.common.constraint.PhoneNumber;
import com.driving.planning.common.hourly.Hourly;
import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@RegisterForReflection
public class MonitorDto implements Serializable {

    public static final long serialVersionUID = 1L;

    private String id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @PhoneNumber
    private String phoneNumber;

    private Set<Hourly> workDays;

    public MonitorDto(){
        workDays = new HashSet<>();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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
        if (workDays != null){
            this.workDays = workDays;
        }
    }

}
