package com.jpabook.jpashop.config;

import com.atomikos.remoting.spring.rest.TransactionAwareRestClientInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // 인터셉터 목록 가져오기
        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        if (interceptors == null) {
            interceptors = new ArrayList<>();
        }

        // TransactionAwareRestClientInterceptor 추가
        interceptors.add(new TransactionAwareRestClientInterceptor());
        restTemplate.setInterceptors(interceptors);

        return restTemplate;
    }
}