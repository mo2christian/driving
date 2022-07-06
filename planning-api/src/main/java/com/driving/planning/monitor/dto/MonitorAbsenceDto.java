package com.driving.planning.monitor.dto;

import com.driving.planning.monitor.absence.Absence;

import java.util.ArrayList;
import java.util.List;

public class MonitorAbsenceDto extends MonitorDto {

    private List<Absence> absences;

    public MonitorAbsenceDto() {
        absences = new ArrayList<>();
    }

    public List<Absence> getAbsences() {
        return absences;
    }

    public void setAbsences(List<Absence> absences) {
        this.absences = absences;
    }
}
