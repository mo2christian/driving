package com.driving.planning.school.student;

import com.driving.planning.client.StudentApiClient;
import com.driving.planning.client.model.StudentReservationDto;
import com.driving.planning.school.common.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/student")
public class StudentController {

    private final StudentApiClient studentApiClient;

    private final StudentMapper mapper;

    private static final String STUDENT_VIEW = "student";

    private static final String REDIRECT_URL = "redirect:/student/list";

    @Autowired
    public StudentController(StudentApiClient studentApiClient, StudentMapper mapper) {
        this.studentApiClient = studentApiClient;
        this.mapper = mapper;
    }

    @GetMapping("/list")
    public String list(){
        return STUDENT_VIEW;
    }

    @GetMapping("/show")
    public String show(@RequestParam("id") String id, Model model){
        var body = studentApiClient.getStudent(id, Utils.getSchoolID());
        if (body == null){
            throw new IllegalStateException();
        }
        var form = mapper.dtoToForm(body.getBody());
        form.setOperation("update");
        model.addAttribute("studentForm", form);
        return STUDENT_VIEW;
    }

    @PostMapping(value = "/action", params = "operation=add")
    public String add(@Valid @ModelAttribute("studentForm") StudentForm form, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return STUDENT_VIEW;
        }
        var dto = mapper.formToDto(form);
        studentApiClient.addStudent(Utils.getSchoolID(), dto);
        return REDIRECT_URL;
    }

    @PostMapping(value = "/action", params = "operation=update")
    public String update(@Valid @ModelAttribute("studentForm") StudentForm form, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return STUDENT_VIEW;
        }
        var dto = mapper.formToDto(form);
        studentApiClient.updateStudent(form.getId(), Utils.getSchoolID(), dto);
        return REDIRECT_URL;
    }

    @ModelAttribute("students")
    private List<StudentReservationDto> getStudents(){
        var body = studentApiClient.getStudents(Utils.getSchoolID())
                .getBody();
        if (body == null){
            throw new IllegalStateException("Body cant be null");
        }
        return body.getStudents();
    }

    @ModelAttribute("studentForm")
    private StudentForm getForm(){
        return new StudentForm();
    }
}
