package com.labelai.controller;

import com.labelai.dto.*;
import com.labelai.service.PipelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/api/pipeline")
@RequiredArgsConstructor
@Slf4j
public class PipelineController {
    private final PipelineService pipelineService;

    @PostMapping("/ocr")
    public ResponseEntity<?> processOcr(@RequestParam("file") MultipartFile file) {
        try {
            log.info("[OCR_START] filename={}, size={}", file.getOriginalFilename(), file.getSize());
            OcrResponse result = pipelineService.processOcr(file);
            log.info("[OCR_SUCCESS] confidence={}", result.getConfidence());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("[OCR_FAILED] filename={}, error={}", file.getOriginalFilename(), e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/structure")
    public ResponseEntity<?> processStructure(@RequestBody StructureRequest request) {
        try {
            log.info("[STRUCTURE_START] language={}", request.getLanguage());
            StructureResponse result = pipelineService.processStructure(request);
            log.info("[STRUCTURE_SUCCESS]");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("[STRUCTURE_FAILED] error={}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/translate")
    public ResponseEntity<?> processTranslate(@RequestBody TranslateRequest request) {
        try {
            log.info("[TRANSLATE_START] targetCountry={}", request.getTargetCountry());
            TranslateResponse result = pipelineService.processTranslate(request);
            log.info("[TRANSLATE_SUCCESS]");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("[TRANSLATE_FAILED] error={}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/html")
    public ResponseEntity<?> processHtml(@RequestBody HtmlGenerateRequest request) {
        try {
            log.info("[HTML_GEN_START] country={}", request.getCountry());
            String html = pipelineService.processHtml(request);
            log.info("[HTML_GEN_SUCCESS]");
            return ResponseEntity.ok(Map.of("html", html));
        } catch (Exception e) {
            log.error("[HTML_GEN_FAILED] error={}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/full")
    public ResponseEntity<?> processFullPipeline(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "targetCountry", required = false) String targetCountry,
            @RequestParam(value = "generateHtml", required = false, defaultValue = "false") Boolean generateHtml) {
        try {
            log.info("[PIPELINE_START] filename={}, targetCountry={}, generateHtml={}", 
                file.getOriginalFilename(), targetCountry, generateHtml);
            
            PipelineRequest request = PipelineRequest.builder()
                .image(file)
                .targetCountry(targetCountry)
                .generateHtml(generateHtml)
                .build();
            
            PipelineResponse result = pipelineService.processFullPipeline(request);
            log.info("[PIPELINE_SUCCESS]");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("[PIPELINE_FAILED] filename={}, error={}", file.getOriginalFilename(), e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/save/scan")
    public ResponseEntity<?> saveScan(
            @RequestParam String itemId,
            @RequestParam String imageUrl,
            @RequestBody OcrResponse ocrResult) {
        try {
            pipelineService.saveScanResult(itemId, imageUrl, ocrResult);
            log.info("[SCAN_SAVED] itemId={}", itemId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (AccessDeniedException e) {
            log.warn("[ACCESS_DENIED] itemId={}, reason={}", itemId, e.getMessage());
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("[SCAN_SAVE_FAILED] itemId={}, error={}", itemId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/save/schema")
    public ResponseEntity<?> saveSchema(
            @RequestParam String itemId,
            @RequestBody Map<String, Object> data) {
        try {
            pipelineService.saveSchemaData(itemId, data);
            log.info("[SCHEMA_SAVED] itemId={}", itemId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (AccessDeniedException e) {
            log.warn("[ACCESS_DENIED] itemId={}, reason={}", itemId, e.getMessage());
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("[SCHEMA_SAVE_FAILED] itemId={}, error={}", itemId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/save/translate")
    public ResponseEntity<?> saveTranslate(
            @RequestParam String itemId,
            @RequestBody Map<String, Object> data) {
        try {
            pipelineService.saveTranslateData(itemId, data);
            log.info("[TRANSLATE_SAVED] itemId={}", itemId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (AccessDeniedException e) {
            log.warn("[ACCESS_DENIED] itemId={}, reason={}", itemId, e.getMessage());
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("[TRANSLATE_SAVE_FAILED] itemId={}, error={}", itemId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/save/sketch")
    public ResponseEntity<?> saveSketch(
            @RequestParam String itemId,
            @RequestBody Map<String, String> body) {
        try {
            pipelineService.saveSketchData(itemId, body.get("html"));
            log.info("[SKETCH_SAVED] itemId={}", itemId);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (AccessDeniedException e) {
            log.warn("[ACCESS_DENIED] itemId={}, reason={}", itemId, e.getMessage());
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("[SKETCH_SAVE_FAILED] itemId={}, error={}", itemId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/get/scan/{itemId}")
    public ResponseEntity<?> getScan(@PathVariable String itemId) {
        try {
            Map<String, Object> data = pipelineService.getScanData(itemId);
            return ResponseEntity.ok(data);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/get/schema/{itemId}")
    public ResponseEntity<?> getSchema(@PathVariable String itemId) {
        try {
            Map<String, Object> data = pipelineService.getSchemaData(itemId);
            return ResponseEntity.ok(data);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/get/translate/{itemId}")
    public ResponseEntity<?> getTranslate(@PathVariable String itemId) {
        try {
            Map<String, Object> data = pipelineService.getTranslateData(itemId);
            return ResponseEntity.ok(data);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/get/sketch/{itemId}")
    public ResponseEntity<?> getSketch(@PathVariable String itemId) {
        try {
            String html = pipelineService.getSketchData(itemId);
            return ResponseEntity.ok(Map.of("html", html));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
