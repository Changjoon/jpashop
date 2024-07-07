package com.jpabook.jpashop.config;

import com.atomikos.remoting.spring.rest.TransactionAwareRestContainerFilter;
import com.atomikos.remoting.twopc.AtomikosRestPort;
import com.atomikos.remoting.twopc.ParticipantsProvider;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AtomikosConfig {

    @Bean
    public FilterRegistrationBean<AtomikosFilter> transactionAwareRestContainerFilter(TransactionAwareRestContainerFilter filter) {
        FilterRegistrationBean<AtomikosFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AtomikosFilter(filter));
        registrationBean.addUrlPatterns("/members/new"); // 적용할 URL 패턴 설정
        return registrationBean;
    }

    @Bean
    public ParticipantsProvider participantsProvider() {
        return new ParticipantsProvider();
    }

    @Bean
    public AtomikosRestPort atomikosRestPort() {
        return new AtomikosRestPort();
    }

    @Bean
    public AtomikosRestPortController atomikosRestPortController(AtomikosRestPort atomikosRestPort) {
        return new AtomikosRestPortController(atomikosRestPort);
    }
}