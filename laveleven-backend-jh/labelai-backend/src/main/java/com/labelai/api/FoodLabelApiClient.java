package com.labelai.api;

import com.labelai.dto.*;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Slf4j
@RequiredArgsConstructor
public class FoodLabelApiClient {
    private final WebClient foodLabelWebClient;
    private final CircuitBreaker foodLabelCircuitBreaker;
    private final Retry foodLabelRetry;
    private final Bulkhead foodLabelBulkhead;

    public OcrResponse extractText(MultipartFile image) throws Exception {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new ByteArrayResource(image.getBytes()))
            .filename(image.getOriginalFilename())
            .contentType(MediaType.parseMediaType(image.getContentType()));
        
        return executeWithResilience(
            () -> foodLabelWebClient.post()
                .uri(uriBuilder -> uriBuilder
                    .path("/ocr")
                    .queryParam("language", "korean")
                    .build())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(OcrResponse.class)
                .block()
        );
    }

    public StructureResponse structureData(StructureRequest request) {
        return executeWithResilience(
            () -> foodLabelWebClient.post()
                .uri("/structure")  // ✅ 경로 확인 완료
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(StructureResponse.class)
                .block()
        );
    }

    public TranslateResponse translate(TranslateRequest request) {
        return executeWithResilience(
            () -> foodLabelWebClient.post()
                .uri("/translate")  // ✅ 경로 확인 완료
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(TranslateResponse.class)
                .block()
        );
    }

    public String generateHtml(HtmlGenerateRequest request) {
        return executeWithResilience(
            () -> foodLabelWebClient.post()
                .uri("/generate_html")  // ✅ 수정: /html → /generate_html
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .block()
        );
    }

    public PipelineResponse processFullPipeline(PipelineRequest request) throws Exception {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new ByteArrayResource(request.getImage().getBytes()))
            .filename(request.getImage().getOriginalFilename());
        
        if (request.getTargetCountry() != null) {
            builder.part("targetCountry", request.getTargetCountry());
        }
        builder.part("generateHtml", String.valueOf(request.getGenerateHtml()));
        
        return executeWithResilience(
            () -> foodLabelWebClient.post()
                .uri("/process")  // ✅ 수정: /pipeline/full → /process
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(PipelineResponse.class)
                .block()
        );
    }

    private <T> T executeWithResilience(java.util.function.Supplier<T> supplier) {
        return Bulkhead.decorateSupplier(foodLabelBulkhead,
            Retry.decorateSupplier(foodLabelRetry,
                CircuitBreaker.decorateSupplier(foodLabelCircuitBreaker, supplier)
            )
        ).get();
    }
}