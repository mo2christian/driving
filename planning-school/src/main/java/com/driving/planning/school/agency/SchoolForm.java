package com.driving.planning.school.agency;

import com.driving.planning.school.common.constraint.PhoneNumber;
import com.driving.planning.school.common.form.WorkDayForm;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SchoolForm {

    private String id;
    @NotBlank
    private String name;
    @NotBlank
    @Size(min = 5)
    private String zipCode;
    @NotBlank
    private String town;
    @NotBlank
    private String path;
    @NotBlank
    @PhoneNumber
    private String phoneNumber;
    @Valid
    @Size(min = 1, max = 7)
    private List<WorkDayForm> workDays;
    @NotBlank
    private String operation;

    public SchoolForm() {
        workDays = new ArrayList<>();
    }
}
