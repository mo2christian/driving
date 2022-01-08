package com.driving.planning.school.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@RegisterForReflection
public class AddressDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank
    private String town;

    @NotBlank
    private String postalCode;

    @NotBlank
    private String path;

    public AddressDto(){
        //default constructor
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
