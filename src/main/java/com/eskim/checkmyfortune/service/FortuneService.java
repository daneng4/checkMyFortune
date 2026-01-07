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

    private final String TAG_PROMPT = "Prompt";
    private final String TAG_RESPONSE = "Response";
	private final ChatClient chatClient;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public FortuneService(ChatModel chatModel){
		this.chatClient = ChatClient.builder(chatModel).build();
	}

	public FortuneResponse createFortune(FortuneRequest request){
		String prompt = generatePrompt(request);

		log.info("[{}] {}",TAG_PROMPT, prompt);

		ChatResponse chatResponse = chatClient.prompt(prompt)
			.call().chatResponse();

		if(chatResponse == null){
			throw new RuntimeException("Chat response is null");
		}

		Generation result = chatResponse.getResult();
		AssistantMessage output = result.getOutput();

        if(output.getText() == null){
            throw new RuntimeException("output is null");
        }

		FortuneResponse response = parseResult(output.getText());
		return formatFortuneText(response);
	}

	private String generatePrompt(FortuneRequest request){
		int currentYear = Year.now().getValue();

		return """
			너는 운세 상담 전문가야.
			아래 사용자 정보를 바탕으로 %s년의 %s를 만들어줘.
			운세 유형에 따른 정확한 답변을 제공해야해.
			
			반드시 아래 JSON 형식으로만 응답해.
			설명 문장이나 다른 텍스트는 절대 포함하지 마.
			
			{
			  "fortune": "운세 내용"
			}
			
			조건:
			- 4문장 정도로 너무 짧지도 않고 길지도 않게 할 것
			- 과장하지 말 것
			- 부드러운 말투
			
			사용자 정보:
			- 이름: %s
			- 성별: %s
			- 생년월일: %s년 %s월 %s일
			""".formatted(
						currentYear,
                        request.fortuneType(),
						request.name(),
						request.gender(),
						request.birthYear(),
						request.birthMonth(),
						request.birthDay()
					);
	}

	private FortuneResponse parseResult(String text){
		String responseJsonText = text.lines()
			.filter(line -> !line.startsWith("```"))
			.reduce("", (a, b) -> a+b);

        log.info("[{}] {}",TAG_RESPONSE, responseJsonText);

		try{
			return objectMapper.readValue(responseJsonText, FortuneResponse.class);
		} catch (JsonProcessingException e){
			throw new RuntimeException(e);
		}
	}

	private FortuneResponse formatFortuneText(FortuneResponse response) {
		String text = response.fortune();

		if (text == null || text.isBlank()) {
			return response;
		}

		String[] sentences = text.split("(?<=\\.)\\s+");

		if (sentences.length <= 2) {
			return response;
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < sentences.length; i++) {
			sb.append(sentences[i]);
			if (i % 2 == 1) {
				sb.append("\n\n"); // 두 문장 뒤 개행
			} else {
				sb.append(" ");
			}
		}

		String formattedText = sb.toString().trim();

		return new FortuneResponse(formattedText);
	}
}
