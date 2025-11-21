package com.labelai.dto.response;

import com.labelai.dto.OcrResponse;
import com.labelai.dto.StructureResponse;
import com.labelai.dto.TranslateResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDetailResponse {
    private String itemName;
    private OcrResponse ocrResult;
    private StructureResponse structureResult;
    private TranslateResponse translateResult;
    private String sketchResult;
}
