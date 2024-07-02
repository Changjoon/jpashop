package com.jpabook.jpashop.tracing;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import java.io.*;

public class ClientHttpResponseWrapper implements ClientHttpResponse {

    private final ClientHttpResponse response;
    private byte[] bodyBytes;

    public ClientHttpResponseWrapper(ClientHttpResponse response) throws IOException {
        this.response = response;
        InputStream body = response.getBody();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int nRead;
        while ((nRead = body.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        bodyBytes = buffer.toByteArray();
    }

    @Override
    public HttpStatus getStatusCode() throws IOException {
        return (HttpStatus) response.getStatusCode();
    }

    @Override
    public String getStatusText() throws IOException {
        return response.getStatusText();
    }

    @Override
    public void close() {
        response.close();
    }

    @Override
    public InputStream getBody() {
        return new ByteArrayInputStream(bodyBytes);
    }

    @Override
    public HttpHeaders getHeaders() {
        return response.getHeaders();
    }

    public String getBodyAsString() throws IOException {
        return new String(bodyBytes, "UTF-8");
    }

    public boolean isSuccessStatusCode() throws IOException {
        return getStatusCode() == HttpStatus.OK;
    }

    public InputStream getInputStream() {
        return getBody();
    }
}