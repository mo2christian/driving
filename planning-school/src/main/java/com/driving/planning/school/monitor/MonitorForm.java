package com.driving.planning.school.monitor;

import com.driving.planning.school.common.constraint.PhoneNumber;
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
public class MonitorForm {

    public MonitorForm(){
        workDays = new ArrayList<>();
    }

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @PhoneNumber
    private String phoneNumber;

    @Valid
    @Size(min = 1, max = 7)
    private List<WorkDayForm> workDays;

}
