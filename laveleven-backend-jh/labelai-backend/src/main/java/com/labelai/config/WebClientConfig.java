package com.labelai.config;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${foodlabel.api.base-url}")
    private String baseUrl;

    @Bean
    public WebClient foodLabelWebClient() {
        return WebClient.builder().baseUrl(baseUrl).build();
    }

    @Bean
    public CircuitBreaker foodLabelCircuitBreaker(CircuitBreakerRegistry registry) {
        return registry.circuitBreaker("foodLabelApi");
    }

    @Bean
    public Retry foodLabelRetry(RetryRegistry registry) {
        return registry.retry("foodLabelApi");
    }

    @Bean
    public Bulkhead foodLabelBulkhead(BulkheadRegistry registry) {
        return registry.bulkhead("foodLabelApi");
    }
}