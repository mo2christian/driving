package com.driving.planning.school.student;

import com.driving.planning.school.common.TimeConstants;
import com.driving.planning.school.common.constraint.BeforeDate;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@BeforeDate(beforeField = "start", afterField = "end", dateFormat = TimeConstants.HOUR_FORMAT)
public class ReservationForm {

    @NotNull
    private LocalDate date;

    @NotNull
    private LocalTime start;

    @NotNull
    private LocalTime end;

    @NotBlank
    private String studentId;

}
