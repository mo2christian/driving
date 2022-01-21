package com.driving.planning.school.monitor;

import com.driving.planning.school.common.constraint.PhoneNumber;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
public class MonitorForm {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @PhoneNumber
    private String phoneNumber;

}
