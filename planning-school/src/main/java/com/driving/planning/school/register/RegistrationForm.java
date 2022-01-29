package com.driving.planning.school.register;

import com.driving.planning.school.common.constraint.Email;
import com.driving.planning.school.common.constraint.PhoneNumber;
import com.driving.planning.school.common.form.WorkDayForm;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class RegistrationForm {

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
    @Valid
    @Size(min = 1, max = 7)
    private List<WorkDayForm> workDays;

    public RegistrationForm(){
        workDays = new ArrayList<>();
    }
}
