package ru.mathleague.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EasyMappingController {

    @GetMapping("/time_problems")
    public String test(){
        return "time_problems";
    }

}
