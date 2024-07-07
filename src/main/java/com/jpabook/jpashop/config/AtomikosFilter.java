package com.jpabook.jpashop.config;

import com.atomikos.remoting.spring.rest.TransactionAwareRestContainerFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;

@Slf4j
@Component
public class AtomikosFilter implements Filter {

    private final TransactionAwareRestContainerFilter transactionAwareRestContainerFilter;

    public AtomikosFilter(TransactionAwareRestContainerFilter transactionAwareRestContainerFilter) {
        this.transactionAwareRestContainerFilter = transactionAwareRestContainerFilter;
    }
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
        log.info("AtomikosFilter init");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        transactionAwareRestContainerFilter.doFilter(servletRequest, servletResponse, filterChain);
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        log.info("AtomikosFilter destroy");
    }
}
