package com.driving.planning.school.register;

import com.driving.planning.client.DrivingSchoolApiClient;
import com.driving.planning.client.model.*;
import com.driving.planning.school.monitor.WorkDayForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class RegisterController {

    private final DrivingSchoolApiClient schoolApiClient;

    @Autowired
    public RegisterController(DrivingSchoolApiClient schoolApiClient) {
        this.schoolApiClient = schoolApiClient;
    }

    @GetMapping("/register")
    public String registerForm(Model model){
        var form = new RegistrationForm();
        for (var day : Day.values()){
            var workday = new WorkDayForm();
            workday.setDay(day);
            workday.setSelected(day != Day.SU);
            workday.setBegin(LocalTime.of(8,0));
            workday.setEnd(LocalTime.of(17, 0));
            form.getWorkDays().add(workday);
        }
        model.addAttribute("request", form);
        return "subscription";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("request") RegistrationForm request,
                           BindingResult result){
        log.info("register new school {}", request);
        if (result.hasErrors()) {
            return "subscription";
        }
        var accountDto = new AccountDto()
                .email(request.getEmail())
                .password(request.getPassword());
        var addressDto = new AddressDto()
                .path(request.getPath())
                .postalCode(request.getZipCode())
                .town(request.getTown());
        var formatter = DateTimeFormatter.ofPattern("HH:mm");
        var workDays = request.getWorkDays()
                .stream()
                .filter(WorkDayForm::isSelected)
                .map(wf -> new Hourly()
                        .day(wf.getDay())
                        .begin(formatter.format(wf.getBegin()))
                        .end(formatter.format(wf.getEnd())))
                .collect(Collectors.toSet());
        var schoolDto = new SchoolDto()
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .address(addressDto)
                .workDays(workDays);
        var schoolRequest = new SchoolRequest()
                .school(schoolDto)
                .account(accountDto);
        schoolApiClient.apiV1SchoolsPost(schoolRequest);
        return "redirect:/login";
    }

}
