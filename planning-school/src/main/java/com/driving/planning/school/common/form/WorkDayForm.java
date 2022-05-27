package com.driving.planning.school.common.form;

import com.driving.planning.client.model.Day;
import com.driving.planning.school.common.constraint.WorkDay;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@WorkDay
@ToString(of = {"day", "selected"})
@EqualsAndHashCode(of = {"day"})
public class WorkDayForm {

    private Day day;
    private LocalTime begin;
    private LocalTime end;
    private boolean selected;

}
