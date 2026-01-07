package com.eskim.checkmyfortune.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
public class HomeController {

    // 첫 화면
    @GetMapping("/")
    public String selectFortune(Model model) {
        int year = LocalDate.now().getYear();
        model.addAttribute("year", year);
        return "select";
    }
}
