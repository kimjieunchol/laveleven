package com.labelai.config;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Value("${external.food-label-api.url}")
    private String foodLabelApiBaseUrl;

    @Bean
    public WebClient foodLabelWebClient() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(30)); // 30-second timeout

        return WebClient.builder()
                .baseUrl(foodLabelApiBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Bean
    public CircuitBreaker foodLabelCircuitBreaker() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(50) // 50% of calls must fail
                .waitDurationInOpenState(Duration.ofMillis(10000)) // 10 seconds
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(10) // 10 calls to determine failure rate
                .build();
        return CircuitBreaker.of("foodLabelService", circuitBreakerConfig);
    }

    @Bean
    public Retry foodLabelRetry() {
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(2000)) // 2 seconds
                .build();
        return Retry.of("foodLabelService", retryConfig);
    }

    @Bean
    public Bulkhead foodLabelBulkhead() {
        return Bulkhead.ofDefaults("foodLabelService");
    }
}