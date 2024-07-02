package com.jpabook.jpashop.tracing;

import jakarta.servlet.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

@Component
@ConditionalOnProperty(name = "apitracing.enabled", havingValue = "true")
public class IncomingReqResFilter implements Filter {

    private static final int MAX_BODY_LENGTH = 1000;
    Logger logger = Logger.getLogger(IncomingReqResFilter.class.getName());


    public IncomingReqResFilter() {

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("Incoming Request/Response Filter Initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        RestApiRequestWrapper httpRequest = new RestApiRequestWrapper(request);
        RestApiResponseWrapper responseWrapper = new RestApiResponseWrapper(response);

        chain.doFilter((ServletRequest) httpRequest, (ServletResponse) responseWrapper);
    }

    private List<Map<String, String>> buildBodyPatterns(RestApiRequestWrapper request) throws IOException {
        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        }

        String bodyContent = body.toString();
        if (bodyContent.length() > MAX_BODY_LENGTH) {
            bodyContent = bodyContent.substring(0, MAX_BODY_LENGTH) + "...(truncated)";
        }

        Map<String, String> map = new HashMap<>();
        map.put("equalToJson", bodyContent);
        return Collections.singletonList(map);
    }


    @Override
    public void destroy() {
        logger.info("Incoming Request/Response Filter Destroyed");
    }
}
