package com.eskim.checkmyfortune.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.eskim.checkmyfortune.dto.FortuneRequest;
import com.eskim.checkmyfortune.dto.FortuneResponse;
import com.eskim.checkmyfortune.service.FortuneService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
@SessionAttributes("fortuneType")
@RequestMapping("/fortune")
public class FortuneController {

	private final String TAG = "FortuneController";
	private final FortuneService fortuneService;

    // 운세 종류 선택
    @PostMapping("/select")
    public String saveFortuneType(
            @RequestParam String fortuneType,
            Model model
    ) {
        model.addAttribute("fortuneType", fortuneType);
        return "redirect:/fortune/input";
    }

    // 사용자 정보 입력 화면
    @GetMapping("/input")
    public String inputUser() {
        return "input";
    }

    // 운세 요청
	@PostMapping("/result")
	public String createFortune(@SessionAttribute("fortuneType") String fortuneType, @RequestParam String name, @RequestParam String age, Model model){
		log.info("[{}] {} {} {}", TAG, name, age, fortuneType);
		FortuneResponse response = fortuneService.createFortune(new FortuneRequest(name, age, fortuneType));

        model.addAttribute("name", name);
        model.addAttribute("age", age);
        model.addAttribute("fortuneType", fortuneType);
        model.addAttribute("response", response);

        return "result";
	}

}
