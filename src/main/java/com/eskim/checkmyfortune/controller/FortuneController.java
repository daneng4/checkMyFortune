package com.eskim.checkmyfortune.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eskim.checkmyfortune.dto.FortuneRequest;
import com.eskim.checkmyfortune.dto.FortuneResponse;
import com.eskim.checkmyfortune.service.FortuneService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class FortuneController {

	private final String TAG = "FortuneController";
	private final FortuneService fortuneService;

	@PostMapping("/fortune")
	public FortuneResponse createFortune(@RequestBody FortuneRequest request){
		log.info("[{}] {} {} {}", TAG, request.name(), request.age(), request.fortuneType());
		return fortuneService.createFortune(request);
	}

}
