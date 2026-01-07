package com.eskim.checkmyfortune.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.Month;

@Controller
public class HomeController {

    // 첫 화면
    @GetMapping("/")
    public String selectFortune(Model model) {
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonth().getValue();
        int day = LocalDate.now().getDayOfMonth();
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("day", day);
        return "select";
    }
}
