package com.driving.planning.school.monitor;

import com.driving.planning.client.DrivingSchoolApiClient;
import com.driving.planning.client.MonitorApiClient;
import com.driving.planning.client.model.Day;
import com.driving.planning.client.model.MonitorDto;
import com.driving.planning.client.model.MonitorResponse;
import com.driving.planning.school.common.exception.ApiException;
import com.driving.planning.school.common.form.WorkDayForm;
import com.driving.planning.school.common.form.WorkDayMapper;
import com.driving.planning.school.config.SchoolAuthenticationDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/monitor")
public class MonitorController {

    private static final String MONITOR_VIEW = "monitor";

    private static final String DISABLED = "disabled";

    private final MonitorApiClient monitorApiClient;

    private final DrivingSchoolApiClient schoolApiClient;

    private final WorkDayMapper workDayMapper;

    private final MonitorMapper monitorMapper;

    @Autowired
    public MonitorController(MonitorApiClient monitorApiClient,
                             DrivingSchoolApiClient schoolApiClient,
                             WorkDayMapper workDayMapper,
                             MonitorMapper monitorMapper) {
        this.monitorApiClient = monitorApiClient;
        this.schoolApiClient = schoolApiClient;
        this.workDayMapper = workDayMapper;
        this.monitorMapper = monitorMapper;
    }

    @GetMapping
    public String list(){
        return MONITOR_VIEW;
    }

    @GetMapping(params = {"id"})
    public String get(Model model, @RequestParam("id") String id){
        var monitor = monitorApiClient.getMonitorById(id, getSchoolID())
                .getBody();
        if (monitor == null){
            throw new ApiException(String.format("Unable to retrieve monitor %s", id));
        }
        var wdMonitor = monitor.getWorkDays()
                .stream()
                .map(workDayMapper::hourlyToForm)
                .collect(Collectors.toList());
        var wdSchool = ((MonitorForm)model.getAttribute("request")).getWorkDays();
        wdSchool.forEach(wd -> wd.setSelected(wdMonitor.contains(wd)));
        var form = monitorMapper.dtoToForm(monitor);
        form.setWorkDays(wdSchool);
        form.setMonitorId(id);
        form.setDisabled(true);
        model.addAttribute("request", form);
        return MONITOR_VIEW;
    }

    @PostMapping
    public String add(@Valid @ModelAttribute("request") MonitorForm form, BindingResult result){
        log.info("Operation on monitor {}", form);
        if (result.hasErrors()){
            return MONITOR_VIEW;
        }
        var workDays = form.getWorkDays()
                .stream()
                .filter(WorkDayForm::isSelected)
                .map(workDayMapper::formToHourly)
                .collect(Collectors.toSet());
        var dto = monitorMapper.formToDto(form)
                .workDays(workDays);
        if (!form.isDisabled()){
            log.info("Adding monitor");
            monitorApiClient.addMonitor(getSchoolID(), dto);
        }
        else{
            log.info("Updating monitor {}", form.getMonitorId());
            monitorApiClient.updateMonitor(form.getMonitorId(), getSchoolID(), dto);
        }
        return "redirect:/monitor";
    }

    @ModelAttribute("request")
    public MonitorForm getForm(){
        var school = schoolApiClient.getSchoolByID(getSchoolID())
                .getBody();
        if (school == null){
            throw new ApiException(String.format("Unable to retrieve school %s", getSchoolID()));
        }
        var wd = school.getWorkDays().stream()
                .map(h -> {
                    var wdf = workDayMapper.hourlyToForm(h);
                    wdf.setSelected(h.getDay() != Day.SU);
                    return wdf;
                })
                .collect(Collectors.toList());
        var form = new MonitorForm();
        form.setDisabled(false);
        form.setWorkDays(wd);
        form.getWorkDays().sort(Comparator.comparing(WorkDayForm::getDay));
        return form;
    }

    @ModelAttribute("monitors")
    public List<MonitorDto> getMonitorList(){
        MonitorResponse response = monitorApiClient.getMonitors(getSchoolID())
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
