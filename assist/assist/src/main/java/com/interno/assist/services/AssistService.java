package com.interno.assist.services;

import com.interno.assist.config.WebClientBuilder;
import com.interno.assist.dao.AssistResponseDao;
import com.interno.assist.dto.AssistRequestDto;
import com.interno.assist.dto.ModelContentDto;
import com.interno.assist.dto.ModelContentPartDto;
import com.interno.assist.dto.ModelRequestDto;
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

        String content = requestDto.getContent();

        StringBuilder promptBuilder =
                new StringBuilder();

        switch (operation) {

            case "summarize":

                promptBuilder.append("""
                        Summarize the following content.

                        Requirements:
                        - Keep the important information.
                        - Remove unnecessary repetition.
                        - Use clear and simple language.
                        - Use a short heading if appropriate.
                        - Use bullet points when they improve readability.
                        - Do not add information that is not present in the original content.

                        Content:
                        
                        """);

                break;

            case "suggest":

                promptBuilder.append("""
                        Analyze the following content and suggest
                        related topics and further reading.

                        Requirements:
                        - Use clear headings.
                        - Use numbered or bullet lists.
                        - Suggest useful related concepts.
                        - Keep the suggestions relevant to the content.
                        - Do not invent unrelated topics.

                        Content:
                        
                        """);

                break;

            case "rewrite":

                promptBuilder.append("""
                        Rewrite the following content.

                        Requirements:
                        - Improve clarity and readability.
                        - Keep the original meaning.
                        - Use professional and natural language.
                        - Do not add unnecessary information.
                        - Preserve important technical terms.

                        Content:
                        
                        """);

                break;

            case "grammar":

                promptBuilder.append("""
                        Correct the grammar of the following content.

                        Requirements:
                        - Correct grammar, spelling and punctuation.
                        - Preserve the original meaning.
                        - Improve sentence structure where necessary.
                        - Do not unnecessarily rewrite the content.
                        - Return the corrected version directly.

                        Content:
                        
                        """);

                break;

            case "explain":

                promptBuilder.append("""
                        Explain the following content in simple and
                        easy-to-understand language.

                        Requirements:
                        - Explain difficult concepts clearly.
                        - Use examples where useful.
                        - Use headings and bullet points where appropriate.
                        - Assume the reader is learning the topic for the first time.
                        - Do not unnecessarily repeat the original text.

                        Content:
                        
                        """);

                break;

            case "improve":

                promptBuilder.append("""
                        Improve the following content.

                        Requirements:
                        - Improve clarity.
                        - Improve grammar.
                        - Improve readability.
                        - Improve sentence structure.
                        - Maintain the original meaning.
                        - Make the content professional and polished.

                        Content:
                        
                        """);

                break;

            case "simplify":

                promptBuilder.append("""
                        Simplify the following content.

                        Requirements:
                        - Use simple and easy-to-understand language.
                        - Preserve the original meaning.
                        - Remove unnecessary complexity.
                        - Keep important technical terms when necessary.
                        - Use short sentences where appropriate.

                        Content:
                        
                        """);

                break;

            case "translate":

                String language = requestDto.getLanguage();

                if (language == null ||
                        language.trim().isEmpty()) {

                    throw new ApplicationRuntimeException(
                            "Translation language is required."
                    );
                }

                promptBuilder.append("""
                        Translate the following content into
                        the requested language.

                        Requirements:
                        - Preserve the original meaning.
                        - Preserve technical terminology where appropriate.
                        - Keep the same structure where possible.
                        - Do not add explanations.
                        - Return only the translated content.

                        Target language:
                        
                        """);

                promptBuilder.append(language);

                promptBuilder.append("""

                        Content:

                        """);

                break;

            default:

                throw new ApplicationRuntimeException(
                        "Unsupported operation: "
                                + requestDto.getOperation()
                );
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