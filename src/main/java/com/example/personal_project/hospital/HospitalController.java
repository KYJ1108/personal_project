package com.example.personal_project.hospital;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/hospital")
public class HospitalController {
    private HospitalService hospitalService;

    @GetMapping("/")
    public String list(Model model) {
        model.addAttribute("hospitals", hospitalService.findAll());
            return "hospital_form";
    }
    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model model){
        Hospital hospital = hospitalService.findById(id);
        model.addAttribute("hospital", hospital);
        return "hospital_form";
    }
}
