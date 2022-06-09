package com.driving.planning.school.student;

import com.driving.planning.school.common.constraint.Email;
import com.driving.planning.school.common.constraint.PhoneNumber;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class StudentForm {

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @Email
    private String email;

    @PhoneNumber
    private String phoneNumber;

    private String id;

    private String operation = "add";

}
