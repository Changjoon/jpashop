package com.jpabook.jpashop.tracing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class AspectConfig {

    @Bean
    public PreparedStatementLoggingAspect preparedStatementLoggingAspect() {
        return new PreparedStatementLoggingAspect();
    }
}
