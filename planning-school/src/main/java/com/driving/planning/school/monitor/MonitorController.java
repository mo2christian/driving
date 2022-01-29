package com.driving.planning.school.monitor;

import com.driving.planning.client.DrivingSchoolApiClient;
import com.driving.planning.client.MonitorApiClient;
import com.driving.planning.client.model.Hourly;
import com.driving.planning.client.model.MonitorDto;
import com.driving.planning.client.model.MonitorResponse;
import com.driving.planning.school.common.TimeConstants;
import com.driving.planning.school.common.exception.ApiException;
import com.driving.planning.school.common.form.WorkDayForm;
import com.driving.planning.school.config.SchoolAuthenticationDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/monitor")
public class MonitorController {

    private final MonitorApiClient monitorApiClient;

    private final DrivingSchoolApiClient schoolApiClient;

    @Autowired
    public MonitorController(MonitorApiClient monitorApiClient, DrivingSchoolApiClient schoolApiClient) {
        this.monitorApiClient = monitorApiClient;
        this.schoolApiClient = schoolApiClient;
    }

    @GetMapping
    public String list(){
        return "monitor";
    }

    @PostMapping
    public String add(@Valid @ModelAttribute("request") MonitorForm form, BindingResult result){
        log.info("Add new monitor {}", form);
        if (result.hasErrors()){
            return "monitor";
        }
        var formatter = DateTimeFormatter.ofPattern(TimeConstants.HOUR_FORMAT.value());
        var workDays = form.getWorkDays()
                .stream()
                .filter(WorkDayForm::isSelected)
                .map(wf -> new Hourly()
                        .day(wf.getDay())
                        .begin(formatter.format(wf.getBegin()))
                        .end(formatter.format(wf.getEnd())))
                .collect(Collectors.toSet());
        var dto = new MonitorDto()
                .firstName(form.getFirstName())
                .lastName(form.getLastName())
                .phoneNumber(form.getPhoneNumber())
                .workDays(workDays);
        monitorApiClient.apiV1MonitorsPost(getSchoolID(), dto);
        return "redirect:/monitor";
    }

    @ModelAttribute("request")
    public MonitorForm getForm(){
        var form = new MonitorForm();
        var school = schoolApiClient.apiV1SchoolsIdGet(getSchoolID())
                .getBody();
        if (school == null){
            throw new ApiException(String.format("Unable to retrieve school %s", getSchoolID()));
        }
        var formatter = DateTimeFormatter.ofPattern(TimeConstants.HOUR_FORMAT.value());
        for (var hourly : school.getWorkDays()){
            var workday = new WorkDayForm();
            workday.setDay(hourly.getDay());
            workday.setBegin(LocalTime.parse(hourly.getBegin(), formatter));
            workday.setEnd(LocalTime.parse(hourly.getEnd(), formatter));
            form.getWorkDays().add(workday);
        }
        form.getWorkDays().sort(Comparator.comparing(WorkDayForm::getDay));
        return form;
    }

    @ModelAttribute("monitors")
    public List<MonitorDto> getMonitorList(){
        MonitorResponse response = monitorApiClient.apiV1MonitorsGet(getSchoolID())
                .getBody();
        if (response != null){
            return response.getMonitors();
        }
        return Collections.emptyList();
    }

    private String getSchoolID(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var authenticationToken = (UsernamePasswordAuthenticationToken)authentication;
        var details = (SchoolAuthenticationDetails)authenticationToken.getDetails();
        return details.getSchool();
    }

}
