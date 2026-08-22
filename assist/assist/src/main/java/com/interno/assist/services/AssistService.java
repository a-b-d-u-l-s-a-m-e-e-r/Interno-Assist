package com.interno.assist.services;

import com.interno.assist.config.WebClientBuilder;
import com.interno.assist.dao.AssistResponseDao;
import com.interno.assist.dto.AssistRequestDto;
import com.interno.assist.dto.ModelContentDto;
import com.interno.assist.dto.ModelContentPartDto;
import com.interno.assist.dto.ModelRequestDto;
import com.interno.assist.exceptionHandling.ApplicationRuntimeException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
public class AssistService {

    @Value("${gemini.api.url}")
    private String MODEL_API_URL;

    @Value("${gemini.api.key}")
    private String MODEL_API_KEY;

    private final WebClientBuilder webClient;

    private final ObjectMapper objectMapper;

    public AssistService(WebClientBuilder webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }


    public String processContent(AssistRequestDto requestDto) {
        //Build th prompt
        String prompt = buildPrompt(requestDto);

        ModelRequestDto modelRequestDto =  buildModelRequestDto(prompt);

        String internoResponse = webClient.webClientBuild().build().post()
                .uri(MODEL_API_URL+MODEL_API_KEY)
                .bodyValue(modelRequestDto)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return parseModelResponse(internoResponse);
    }

    private String parseModelResponse(String modelResponse) {
        try {
            AssistResponseDao responseDao = objectMapper.readValue(modelResponse, AssistResponseDao.class);
            if(responseDao.getCandidates() != null && !responseDao.getCandidates().isEmpty()) {
                AssistResponseDao.Candidate candidate = responseDao.getCandidates().get(0);
                if(candidate.getContent() != null && candidate.getContent().getParts() != null && !candidate.getContent().getParts().isEmpty()) {
                    return candidate.getContent().getParts().get(0).getText();
                }
            }
            return "No content found in the model response.";
        }catch (Exception e) {
            throw new ApplicationRuntimeException("Failed to parse model response"+ e.getMessage());
        }
    }



    private ModelRequestDto buildModelRequestDto(String prompt) {
     return ModelRequestDto.builder()
                        .contents(List.of(ModelContentDto.builder()
                                .parts(List.of(ModelContentPartDto.builder()
                                        .text(prompt)
                                        .build()))
                                .build()))
                                .build();
    }
    
    private String buildPrompt(AssistRequestDto requestDto) {
        //Build the prompt based on the requestDto
        StringBuilder promptBuilder = new StringBuilder();
        switch(requestDto.getOperation()) {
            case "summarize":
                promptBuilder.append("Summarize the following content:\n\n");
                break;
            case "suggest":
                promptBuilder.append("Based on the following content: suggest related topics and further reading. Format the response with clear headings and bullet points:\n\n");
                break;
            default:
                throw new ApplicationRuntimeException("Unsupported operation: " + requestDto.getOperation());
        }
        promptBuilder.append(requestDto.getContent());
        return promptBuilder.toString();
    }
}
