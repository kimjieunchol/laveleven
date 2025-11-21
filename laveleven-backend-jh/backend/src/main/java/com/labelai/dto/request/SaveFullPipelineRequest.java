package com.labelai.dto.request;

import com.labelai.dto.OcrResponse;
import com.labelai.dto.StructureResponse;
import com.labelai.dto.TranslateResponse;
import lombok.Data;

/**
 * 전체 파이프라인 결과를 저장하기 위한 요청 DTO
 */
@Data
public class SaveFullPipelineRequest {
    // Item 정보
    private String itemName;
    private String itemType;

    // Scan 정보
    private String originalFileName;
    private OcrResponse ocrResult;

    // Schema 정보
    private StructureResponse structureResult;

    // Translate 정보
    private TranslateResponse translateResult;

    // Sketch 정보
    private String sketchResult; // HTML string
}
