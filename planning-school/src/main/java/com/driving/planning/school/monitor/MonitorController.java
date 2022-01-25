package com.driving.planning.school.monitor;

import com.driving.planning.client.MonitorApiClient;
import com.driving.planning.client.model.Day;
import com.driving.planning.client.model.Hourly;
import com.driving.planning.client.model.MonitorDto;
import com.driving.planning.client.model.MonitorResponse;
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
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/monitor")
public class MonitorController {

    private final MonitorApiClient monitorApiClient;


    @Autowired
    public MonitorController(MonitorApiClient monitorApiClient) {
        this.monitorApiClient = monitorApiClient;
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
        var formatter = DateTimeFormatter.ofPattern("HH:mm");
        var workDays = form.getWorkDays()
                .stream().map(wf -> new Hourly()
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
        for (var day : Day.values()){
            var workday = new WorkDayForm();
            workday.setDay(day);
            workday.setBegin(LocalTime.of(9,0));
            workday.setEnd(LocalTime.of(18, 0));
            form.getWorkDays().add(workday);
        }
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
