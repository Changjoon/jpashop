package com.jpabook.jpashop.config;

import com.atomikos.remoting.spring.rest.TransactionAwareRestContainerFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<TransactionAwareRestContainerFilter> transactionAwareFilter() {
        FilterRegistrationBean<TransactionAwareRestContainerFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TransactionAwareRestContainerFilter());
        registrationBean.addUrlPatterns("/*"); // 모든 URL 패턴에 대해 필터 적용
        registrationBean.setOrder(1); // 필터의 우선 순위 설정 (낮을수록 높은 우선 순위)
        return registrationBean;
    }
}