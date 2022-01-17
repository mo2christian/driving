package com.driving.planning.school.login;

import com.driving.planning.client.DrivingSchoolApiClient;
import com.driving.planning.client.model.SchoolDto;
import com.driving.planning.school.common.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Controller
public class LoginController {

    private final DrivingSchoolApiClient schoolApiClient;

    @Autowired
    public LoginController(DrivingSchoolApiClient schoolApiClient){
        this.schoolApiClient = schoolApiClient;
    }

    @GetMapping("/login")
    public String login(Model model){
        log.debug("Init login page");
        List<SchoolDto> schools = new ArrayList<>();
        try{
            schools = Objects.requireNonNull(schoolApiClient.apiV1SchoolsGet()
                            .getBody())
                    .getSchools();
        }
        catch(ApiException ex){
            log.debug("Error while getting schools", ex);
        }
        model.addAttribute("schools", schools);
        return "login";
    }

}
