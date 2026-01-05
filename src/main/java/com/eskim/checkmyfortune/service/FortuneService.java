package com.eskim.checkmyfortune.service;

import java.time.Year;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.stereotype.Service;

import com.eskim.checkmyfortune.dto.FortuneRequest;
import com.eskim.checkmyfortune.dto.FortuneResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FortuneService {

	private final ChatClient chatClient;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public FortuneService(ChatModel chatModel){
		this.chatClient = ChatClient.builder(chatModel).build();
	}

	public FortuneResponse createFortune(FortuneRequest request){
		String prompt = generatePrompt(request);

		log.info("[Prompt] {}", prompt);

		ChatResponse chatResponse = chatClient.prompt(prompt)
			.call().chatResponse();

		if(chatResponse == null){
			throw new RuntimeException("Chat response is null");
		}

		Generation result = chatResponse.getResult();
		AssistantMessage output = result.getOutput();

		return parseResult(output.getText());
	}

	private String generatePrompt(FortuneRequest request){
		int currentYear = Year.now().getValue();

		return """
			너는 운세 상담 전문가야.
			아래 사용자 정보를 바탕으로 %s년의 %s 운세를 만들어줘.
			
			반드시 아래 JSON 형식으로만 응답해.
			설명 문장이나 다른 텍스트는 절대 포함하지 마.
			
			{
			  "fortune": "운세 내용"
			}
			
			조건:
			- 3문장 이내
			- 과장하지 말 것
			- 부드러운 말투
			
			사용자 정보:
			- 이름: %s
			- 나이: %d
			""".formatted(
						currentYear,
						request.fortuneType(),
						request.name(),
						request.age()
					);
	}

	private FortuneResponse parseResult(String text){
		String jsonText = text.lines()
			.filter(line -> !line.startsWith("```"))
			.reduce("", (a, b) -> a+b);

		try{
			return objectMapper.readValue(jsonText, FortuneResponse.class);
		} catch (JsonProcessingException e){
			throw new RuntimeException(e);
		}
	}
}
