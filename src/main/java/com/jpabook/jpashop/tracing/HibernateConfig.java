package com.jpabook.jpashop.tracing;

import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Configuration
public class HibernateConfig {

    @Autowired
    private CustomInterceptor customInterceptor;

    @Autowired
    private CustomStatementInspector customStatementInspector;

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer() {
        return new HibernatePropertiesCustomizer() {
            @Override
            public void customize(Map<String, Object> hibernateProperties) {
                hibernateProperties.put("hibernate.session_factory.interceptor", customInterceptor);
                hibernateProperties.put("hibernate.session_factory.statement_inspector", customStatementInspector);
            }
        };
    }
}
