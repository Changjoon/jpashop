package com.jpabook.jpashop.tracing;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConditionalOnProperty(name = "tracing.enabled", havingValue = "true")
public class RestTemplateConfig {
    private final ClientHttpRequestInterceptor clientHttpRequestInterceptor;

    public RestTemplateConfig(ClientHttpRequestInterceptor clientHttpRequestInterceptor) {
        this.clientHttpRequestInterceptor = clientHttpRequestInterceptor;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.additionalInterceptors(clientHttpRequestInterceptor).build();
    }
}
