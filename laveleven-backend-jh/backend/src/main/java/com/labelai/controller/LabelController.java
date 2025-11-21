package com.labelai.controller;

import com.labelai.dto.*; // Added for new DTOs
import com.labelai.dto.request.SaveFullPipelineRequest;
import com.labelai.entity.Item;
import com.labelai.service.LabelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/label")
@RequiredArgsConstructor
public class LabelController {

    private final LabelService labelService;

    // =================================================================
    // Pipeline Initialization and Step History Logging Endpoints
    // =================================================================

    @PostMapping("/start-pipeline")
    public ResponseEntity<ItemIdResponse> startPipeline(@RequestBody StartPipelineRequest request, Authentication auth) {
        String username = auth.getName();
        Item item = labelService.startPipeline(username, request.getItemName(), request.getItemType());
        return ResponseEntity.ok(ItemIdResponse.builder().itemId(item.getItemId().toString()).build());
    }

    @PostMapping("/log-ocr-history/{itemId}")
    public ResponseEntity<OcrResultWithItemId> logOcrHistory(
            @PathVariable String itemId,
            @RequestParam("image") MultipartFile image,
            Authentication auth
    ) throws IOException {
        String username = auth.getName();
        return ResponseEntity.ok(labelService.logOcrHistory(username, itemId, image));
    }

    @PostMapping("/log-structure-history/{itemId}")
    public ResponseEntity<StructureResponseWithItemId> logStructureHistory(
            @PathVariable String itemId,
            @RequestBody StructureRequest request,
            Authentication auth
    ) {
        String username = auth.getName();
        return ResponseEntity.ok(labelService.logStructureHistory(username, itemId, request));
    }

    @PostMapping("/log-translate-history/{itemId}")
    public ResponseEntity<TranslateResponseWithItemId> logTranslateHistory(
            @PathVariable String itemId,
            @RequestBody TranslateRequest request,
            Authentication auth
    ) {
        String username = auth.getName();
        return ResponseEntity.ok(labelService.logTranslateHistory(username, itemId, request));
    }

    // =================================================================
    // Final Data Persistence Endpoint
    // =================================================================

    @PostMapping("/pipeline/{itemId}")
    public ResponseEntity<Void> saveFullPipeline(
            @PathVariable String itemId,
            @RequestBody SaveFullPipelineRequest request,
            Authentication auth
    ) {
        String username = auth.getName();
        labelService.saveFullPipeline(username, itemId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/transient/generate-html")
    public ResponseEntity<String> runTransientGenerateHtml(@RequestBody HtmlGenerateRequest request) {
        String htmlContent = labelService.runTransientGenerateHtml(request);
        return ResponseEntity.ok(htmlContent);
    }
}