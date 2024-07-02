package com.jpabook.jpashop.tracing;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.Logger;

@Primary
@Component
@ConditionalOnProperty(name = "apitracing.enabled", havingValue = "true")
public class OutgoingReqResInterceptor implements ClientHttpRequestInterceptor {

    Logger logger = Logger.getLogger(OutgoingReqResInterceptor.class.getName());

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        ClientHttpResponseWrapper response = new ClientHttpResponseWrapper(execution.execute(request, body));

        logger.info("Request URI: " + request.getURI());
        logger.info("Request Method: " + request.getMethod());
        logger.info("Request Headers: " + request.getHeaders());
        logger.info("Request Body: " + new String(body, "UTF-8"));
        logger.info("Response Status Code: " + response.getStatusCode());
        logger.info("Response Status Text: " + response.getStatusText());
        logger.info("Response Headers: " + response.getHeaders());
        logger.info("Response Body: " + response.getBodyAsString());

        return response;
    }
}
