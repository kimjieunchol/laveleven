package com.labelai.service;

import com.labelai.api.FoodLabelApiClient;
import com.labelai.dto.*;
import com.labelai.entity.*;
import com.labelai.repository.*;
import com.labelai.security.CheckOwnership;
import com.labelai.security.OwnershipType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PipelineService {
    private final FoodLabelApiClient apiClient;
    private final ItemRepository itemRepository;
    private final ScanRepository scanRepository;
    private final SchemaDataRepository schemaDataRepository;
    private final TranslateRepository translateRepository;
    private final SketchRepository sketchRepository;
    private final ObjectMapper objectMapper;

    public OcrResponse processOcr(MultipartFile image) throws Exception {
        return apiClient.extractText(image);
    }

    public StructureResponse processStructure(StructureRequest request) {
        return apiClient.structureData(request);
    }

    public TranslateResponse processTranslate(TranslateRequest request) {
        return apiClient.translate(request);
    }

    public String processHtml(HtmlGenerateRequest request) {
        return apiClient.generateHtml(request);
    }

    public PipelineResponse processFullPipeline(PipelineRequest request) throws Exception {
        return apiClient.processFullPipeline(request);
    }

    @Transactional
    @CheckOwnership(OwnershipType.ITEM)
    public void saveScanResult(String itemId, String imageUrl, OcrResponse ocrResult) throws Exception {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("작업을 찾을 수 없습니다"));
        
        Scan scan = Scan.builder()
            .id(UUID.randomUUID().toString())
            .item(item)
            .scanImageUrl(imageUrl)
            .scanMeta(objectMapper.writeValueAsString(ocrResult))
            .build();
        
        scanRepository.save(scan);
        log.info("Scan 데이터 저장 완료: itemId={}", itemId);
    }

    @Transactional
    @CheckOwnership(OwnershipType.ITEM)
    public void saveSchemaData(String itemId, Map<String, Object> data) throws Exception {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("작업을 찾을 수 없습니다"));
        
        SchemaData schema = SchemaData.builder()
            .id(UUID.randomUUID().toString())
            .item(item)
            .data(objectMapper.writeValueAsString(data))
            .build();
        
        schemaDataRepository.save(schema);
        log.info("Schema 데이터 저장 완료: itemId={}", itemId);
    }

    @Transactional
    @CheckOwnership(OwnershipType.ITEM)
    public void saveTranslateData(String itemId, Map<String, Object> data) throws Exception {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("작업을 찾을 수 없습니다"));
        
        Translate translate = Translate.builder()
            .id(UUID.randomUUID().toString())
            .item(item)
            .data(objectMapper.writeValueAsString(data))
            .build();
        
        translateRepository.save(translate);
        log.info("Translate 데이터 저장 완료: itemId={}", itemId);
    }

    @Transactional
    @CheckOwnership(OwnershipType.ITEM)
    public void saveSketchData(String itemId, String htmlData) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("작업을 찾을 수 없습니다"));
        
        Sketch sketch = Sketch.builder()
            .id(UUID.randomUUID().toString())
            .item(item)
            .data(htmlData)
            .build();
        
        sketchRepository.save(sketch);
        log.info("Sketch 데이터 저장 완료: itemId={}", itemId);
    }

    @CheckOwnership(OwnershipType.ITEM)
    public Map<String, Object> getScanData(String itemId) throws Exception {
        List<Scan> scans = scanRepository.findByItemId(itemId);
        return Map.of("scans", scans);
    }

    @CheckOwnership(OwnershipType.ITEM)
    public Map<String, Object> getSchemaData(String itemId) throws Exception {
        SchemaData schema = schemaDataRepository.findByItemId(itemId)
            .orElseThrow(() -> new IllegalArgumentException("스키마 데이터를 찾을 수 없습니다"));
        return objectMapper.readValue(schema.getData(), Map.class);
    }

    @CheckOwnership(OwnershipType.ITEM)
    public Map<String, Object> getTranslateData(String itemId) throws Exception {
        Translate translate = translateRepository.findByItemId(itemId)
            .orElseThrow(() -> new IllegalArgumentException("번역 데이터를 찾을 수 없습니다"));
        return objectMapper.readValue(translate.getData(), Map.class);
    }

    @CheckOwnership(OwnershipType.ITEM)
    public String getSketchData(String itemId) {
        Sketch sketch = sketchRepository.findByItemId(itemId)
            .orElseThrow(() -> new IllegalArgumentException("스케치 데이터를 찾을 수 없습니다"));
        return sketch.getData();
    }
}
