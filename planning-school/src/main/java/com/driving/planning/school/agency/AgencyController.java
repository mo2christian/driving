package com.driving.planning.school.agency;

import com.driving.planning.client.DrivingSchoolApiClient;
import com.driving.planning.school.common.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/school")
public class AgencyController {

    private final DrivingSchoolApiClient schoolApiClient;

    private final SchoolMapper mapper;

    @Autowired
    public AgencyController(DrivingSchoolApiClient schoolApiClient,
                            SchoolMapper mapper) {
        this.schoolApiClient = schoolApiClient;
        this.mapper = mapper;
    }

    @GetMapping("/list")
    public String get(){
        return "school";
    }

    @PostMapping(value = "/action", params = "operation=update")
    public String update(@Valid @ModelAttribute("schoolForm") SchoolForm form){
        var dto = mapper.formToDto(form);
        schoolApiClient.updateSchool(form.getId(), dto);
        return "redirect:/school/list";
    }

    @ModelAttribute("request")
    private SchoolForm getSchool(){
        var dto = schoolApiClient.getSchoolByID(Utils.getSchoolID())
                .getBody();
        return mapper.dtoToForm(dto);
    }
}
