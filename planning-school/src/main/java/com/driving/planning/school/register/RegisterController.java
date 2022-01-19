package com.driving.planning.school.register;

import com.driving.planning.client.DrivingSchoolApiClient;
import com.driving.planning.client.model.AccountDto;
import com.driving.planning.client.model.AddressDto;
import com.driving.planning.client.model.SchoolDto;
import com.driving.planning.client.model.SchoolRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

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
        RegistrationDto registrationDto = new RegistrationDto();
        model.addAttribute("request", registrationDto);
        return "subscription";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("request") RegistrationDto request,
                           BindingResult result){
        log.info("register new school {}", request);
        if (result.hasErrors()) {
            return "subscription";
        }
        AccountDto accountDto = new AccountDto()
                .email(request.getEmail())
                .password(request.getPassword());
        AddressDto addressDto = new AddressDto()
                .path(request.getPath())
                .postalCode(request.getZipCode())
                .town(request.getTown());
        SchoolDto schoolDto = new SchoolDto()
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .address(addressDto);
        SchoolRequest schoolRequest = new SchoolRequest()
                .school(schoolDto)
                .account(accountDto);
        schoolApiClient.apiV1SchoolsPost(schoolRequest);
        return "redirect:/login";
    }

}
