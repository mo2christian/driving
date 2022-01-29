package com.driving.planning.school.common.form;

import com.driving.planning.client.model.Day;
import com.driving.planning.school.common.constraint.WorkDay;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalTime;

@Getter
@Setter
@ToString
@WorkDay
public class WorkDayForm {

    private Day day;
    private LocalTime begin;
    private LocalTime end;
    private boolean selected = true;

}
