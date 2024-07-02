package com.jpabook.jpashop.tracing;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class RestApiResponseWrapper extends HttpServletResponseWrapper {
    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    private ServletOutputStream outputStream;
    private PrintWriter writer;

    public RestApiResponseWrapper(ServletResponse response) {
        super((HttpServletResponse) response);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (writer != null) {
            throw new IllegalStateException("getWriter() has already been called on this response.");
        }

        if (outputStream == null) {
            outputStream = new ServletOutputStream() {
                @Override
                public void write(int b) throws IOException {
                    byteArrayOutputStream.write(b);
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setWriteListener(WriteListener writeListener) {

                }
            };
        }
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (outputStream != null) {
            throw new IllegalStateException("getOutputStream() has already been called on this response.");
        }

        if (writer == null) {
            writer = new PrintWriter(byteArrayOutputStream);
        }
        return writer;
    }

    public byte[] toByteArray() {
        return byteArrayOutputStream.toByteArray();
    }
}
