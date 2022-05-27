package com.driving.planning.school.monitor;

import com.driving.planning.school.common.constraint.BeforeDate;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@BeforeDate(beforeField = "start", afterField = "end", allowEqual = true)
public class AbsentForm {

    @NotNull
    private LocalDate start;

    @NotNull
    private LocalDate end;

    @NotBlank
    private String monitorId;

}
