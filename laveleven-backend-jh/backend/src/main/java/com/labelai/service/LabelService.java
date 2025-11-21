package com.labelai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.labelai.api.FoodLabelApiClient;
import com.labelai.dto.*;
import com.labelai.dto.request.SaveFullPipelineRequest;
import com.labelai.entity.*;
import com.labelai.exception.CustomException;
import com.labelai.repository.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication; // New import
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant; // Added for Instant.now()
import java.util.Optional; // Added for Optional
import java.util.UUID; // Added for UUID.fromString()


@Slf4j
@Service
@RequiredArgsConstructor
public class LabelService {

    private final FoodLabelApiClient foodLabelApiClient;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ScanRepository scanRepository;
    private final SchemaRepository schemaRepository;
    private final TranslateRepository translateRepository;
    private final SketchRepository sketchRepository;
    private final HistoryService historyService;
    private final ObjectMapper objectMapper;

    // =================================================================
    // Initial Pipeline Setup
    // =================================================================
    @Transactional
    public Item startPipeline(String username, String itemName, String itemType) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("NOT_FOUND", "User not found", 404));

        Item item = Item.builder()
                .itemName(itemName)
                .itemType(itemType)
                .createdBy(user)
                .updatedBy(user)
                .createdAt(Instant.now().atOffset(ZoneOffset.UTC)) // Explicitly set createdAt
                .updatedAt(Instant.now().atOffset(ZoneOffset.UTC)) // Explicitly set updatedAt
                .build();
        item = itemRepository.save(item);
        historyService.addHistoryRecord(item, user, "ITEM", "item", "CREATE", null);
        return item;
    }

    // =================================================================
    // History Logging Methods (no data persistence yet for intermediate steps)
    // =================================================================

    public OcrResultWithItemId logOcrHistory(String username, String itemId, MultipartFile image) throws IOException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("NOT_FOUND", "User not found", 404));
        Item item = itemRepository.findById(UUID.fromString(itemId))
                .orElseThrow(() -> new CustomException("NOT_FOUND", "Item not found", 404));

        OcrResponse ocrResponse = foodLabelApiClient.extractText(image);

        // Only log history, do NOT save Scan data yet
        historyService.addHistoryRecord(item, user, "SCAN", "scan_meta", "OCR_DONE", null);

        return OcrResultWithItemId.builder()
                .itemId(itemId)
                .ocrResponse(ocrResponse)
                .build();
    }

    public StructureResponseWithItemId logStructureHistory(String username, String itemId, StructureRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("NOT_FOUND", "User not found", 404));
        Item item = itemRepository.findById(UUID.fromString(itemId))
                .orElseThrow(() -> new CustomException("NOT_FOUND", "Item not found", 404));

        StructureResponse structureResponse = foodLabelApiClient.structureData(request);

        // Only log history, do NOT save Schema data yet
        historyService.addHistoryRecord(item, user, "SCHEMA", "data", "STRUCTURE_DONE", null);

        return StructureResponseWithItemId.builder()
                .itemId(itemId)
                .structureResponse(structureResponse)
                .build();
    }
    
    public TranslateResponseWithItemId logTranslateHistory(String username, String itemId, TranslateRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("NOT_FOUND", "User not found", 404));
        Item item = itemRepository.findById(UUID.fromString(itemId))
                .orElseThrow(() -> new CustomException("NOT_FOUND", "Item not found", 404));

        TranslateResponse translateResponse = foodLabelApiClient.translate(request);

        // Only log history, do NOT save Translate data yet
        historyService.addHistoryRecord(item, user, "TRANSLATE", "data", "TRANSLATE_DONE", null);

        return TranslateResponseWithItemId.builder()
                .itemId(itemId)
                .translateResponse(translateResponse)
                .build();
    }


    public String runTransientGenerateHtml(HtmlGenerateRequest request) {
        return foodLabelApiClient.generateHtml(request);
    }


    // =================================================================
    // Final Data Persistence Endpoint - Saves all pipeline results and logs only SKETCH history
    // =================================================================

    @Transactional
    public void saveFullPipeline(String username, String itemId, SaveFullPipelineRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("NOT_FOUND", "User not found", 404));
        Item item = itemRepository.findById(UUID.fromString(itemId))
                .orElseThrow(() -> new CustomException("NOT_FOUND", "Item not found", 404));

        // Update Item's updated_at and updated_by
        item.setUpdatedBy(user);
        item.setUpdatedAt(OffsetDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
        itemRepository.save(item);

        try {
            // 2. Scan 결과 저장 (이미 존재하면 업데이트, 없으면 새로 생성)
            if (request.getOcrResult() != null) {
                Scan scan = Scan.builder()
                        .item(item)
                        .scanImageUrl(request.getOriginalFileName())
                        .scanMeta(objectMapper.writeValueAsString(request.getOcrResult()))
                        .build();
                scanRepository.save(scan);
            }

            // 3. Schema 결과 저장
            if (request.getStructureResult() != null) {
                Schema schema = Schema.builder()
                        .item(item)
                        .data(objectMapper.writeValueAsString(request.getStructureResult()))
                        .build();
                schemaRepository.save(schema);
            }

            // 4. Translate 결과 저장
            if (request.getTranslateResult() != null) {
                Translate translate = Translate.builder()
                        .item(item)
                        .data(objectMapper.writeValueAsString(request.getTranslateResult()))
                        .build();
                translateRepository.save(translate);
            }

            // 5. Sketch 결과 저장
            if (request.getSketchResult() != null) {
                // Check if Sketch already exists for this item
                Optional<Sketch> existingSketch = sketchRepository.findFirstByItem(item);

                Sketch sketch;
                if (existingSketch.isPresent()) {
                    // Update existing sketch
                    sketch = existingSketch.get();
                    sketch.setData(request.getSketchResult());
                } else {
                    // Create new sketch
                    sketch = Sketch.builder()
                            .item(item)
                            .data(request.getSketchResult())
                            .build();
                }
                sketchRepository.save(sketch); // Save or update the sketch

                // Only SKETCH history here - final save event
                String serializedSketchResult = objectMapper.writeValueAsString(request.getSketchResult());
                log.info("Serialized SKETCH result for history payload: {}", serializedSketchResult);
                historyService.addHistoryRecord(item, user, "SKETCH", "data", "SAVE", serializedSketchResult);
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize pipeline data to JSON", e);
            throw new CustomException("SERIALIZATION_ERROR", "파이프라인 결과를 저장하는 데 실패했습니다.", 500);
        }
    }
}