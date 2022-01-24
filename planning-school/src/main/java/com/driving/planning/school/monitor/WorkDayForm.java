package com.driving.planning.school.monitor;

import com.driving.planning.client.model.Day;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@Getter
@Setter
@ToString
public class WorkDayForm {

    @NotNull
    private Day day;
    @NotNull
    private LocalTime begin;
    @NotNull
    private LocalTime end;

}
