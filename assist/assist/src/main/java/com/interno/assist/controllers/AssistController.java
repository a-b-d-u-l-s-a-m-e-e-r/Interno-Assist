package com.interno.assist.controllers;


import com.interno.assist.dto.AssistRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.interno.assist.services.AssistService;

@RestController
@RequestMapping("/api/assist")
@CrossOrigin("*")
@AllArgsConstructor
public class AssistController {

    private final AssistService assistService;

    @PostMapping("/process")
    public ResponseEntity<String> processContent(@RequestBody AssistRequestDto requestDto) {
        String content = assistService.processContent(requestDto);
        return ResponseEntity.ok(content);
    }
}
