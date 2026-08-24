package com.interno.assist.controllers;


import com.interno.assist.dto.AssistRequestDto;
import com.interno.assist.dto.ExportRequestDto;
import com.interno.assist.services.ImportExportService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.interno.assist.services.AssistService;

@RestController
@RequestMapping("/api/assist")
@CrossOrigin("*")
@AllArgsConstructor
public class AssistController {

    private final AssistService assistService;
    private final ImportExportService importExportService;

    @PostMapping("/process")
    public ResponseEntity<String> processContent(@RequestBody AssistRequestDto requestDto) {
        String content = assistService.processContent(requestDto);
        return ResponseEntity.ok(content);
    }

    @PostMapping("/download")
    public ResponseEntity<byte[]> processDownload(@RequestBody ExportRequestDto requestDto) {
        byte[] pdf = importExportService.processDownload(requestDto);
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"note.pdf\""
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
