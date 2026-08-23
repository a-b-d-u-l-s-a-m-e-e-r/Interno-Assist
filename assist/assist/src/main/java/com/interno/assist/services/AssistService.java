package com.interno.assist.services;

import com.interno.assist.config.WebClientBuilder;
import com.interno.assist.dao.AssistResponseDao;
import com.interno.assist.dto.AssistRequestDto;
import com.interno.assist.dto.ModelContentDto;
import com.interno.assist.dto.ModelContentPartDto;
import com.interno.assist.dto.ModelRequestDto;
import com.interno.assist.enums.PromptEnum;
import com.interno.assist.exceptionHandling.ApplicationRuntimeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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

    private final String TRANSLATE = "translate";

    public AssistService(
            WebClientBuilder webClient,
            ObjectMapper objectMapper
    ) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    public String processContent(AssistRequestDto requestDto) {

        if (requestDto == null) {
            throw new ApplicationRuntimeException(
                    "Request cannot be null."
            );
        }

        if (requestDto.getContent() == null ||
                requestDto.getContent().trim().isEmpty()) {

            throw new ApplicationRuntimeException(
                    "Content cannot be empty."
            );
        }

        String prompt = buildPrompt(requestDto);

        ModelRequestDto modelRequestDto =
                buildModelRequestDto(prompt);

        String internoResponse =
                webClient
                        .webClientBuild()
                        .build()
                        .post()
                        .uri(MODEL_API_URL + MODEL_API_KEY)
                        .bodyValue(modelRequestDto)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

        return parseModelResponse(internoResponse);
    }

    private String buildPrompt(AssistRequestDto requestDto) {

        String operation = requestDto
                .getOperation()
                .trim()
                .toLowerCase();

        PromptEnum promptEnum = PromptEnum.valueOf(operation.toUpperCase());

        String content = requestDto.getContent();

        StringBuilder promptBuilder =
                new StringBuilder();

        if(TRANSLATE.equals(promptEnum.getCode())) {
            String language = requestDto.getLanguage();

            if (language == null ||
                    language.trim().isEmpty()) {

                throw new ApplicationRuntimeException(
                        "Translation language is required."
                );
            }

            promptBuilder.append(promptEnum.getValue());
            promptBuilder.append(language);
            promptBuilder.append("\n\nContent:\n\n");
        } else {
            promptBuilder.append(promptEnum.getValue());
        }

        promptBuilder.append(content);

        return promptBuilder.toString();
    }

    private ModelRequestDto buildModelRequestDto(
            String prompt
    ) {

        return ModelRequestDto.builder()
                .contents(
                        List.of(
                                ModelContentDto.builder()
                                        .parts(
                                                List.of(
                                                        ModelContentPartDto
                                                                .builder()
                                                                .text(prompt)
                                                                .build()
                                                )
                                        )
                                        .build()
                        )
                )
                .build();
    }

    private String parseModelResponse(
            String modelResponse
    ) {

        try {

            AssistResponseDao responseDao =
                    objectMapper.readValue(
                            modelResponse,
                            AssistResponseDao.class
                    );

            if (
                    responseDao.getCandidates() != null &&
                            !responseDao.getCandidates().isEmpty()
            ) {

                AssistResponseDao.Candidate candidate =
                        responseDao.getCandidates().get(0);

                if (
                        candidate.getContent() != null &&
                                candidate.getContent().getParts() != null &&
                                !candidate.getContent().getParts().isEmpty()
                ) {

                    return candidate
                            .getContent()
                            .getParts()
                            .get(0)
                            .getText();
                }
            }

            return "No content found in the model response.";

        } catch (Exception e) {

            throw new ApplicationRuntimeException(
                    "Failed to parse model response: "
                            + e.getMessage()
            );
        }
    }
}