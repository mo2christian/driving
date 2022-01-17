package com.driving.planning.school.register;

import com.driving.planning.school.common.constraint.Email;
import com.driving.planning.school.common.constraint.PhoneNumber;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
public class RegistrationDto implements Serializable {

    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String name;
    @NotBlank
    @PhoneNumber
    private String phoneNumber;
    @NotBlank
    private String path;
    @NotBlank
    @Size(min = 5)
    private String zipCode;
    @NotBlank
    private String town;

}
