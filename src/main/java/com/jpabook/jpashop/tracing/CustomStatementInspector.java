package com.jpabook.jpashop.tracing;

import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CustomStatementInspector implements StatementInspector {

    private static final Pattern PARAM_PATTERN = Pattern.compile("\\?");
    private ThreadLocal<String> currentSql = new ThreadLocal<>();

    @Override
    public String inspect(String sql) {
        currentSql.set(sql);
        return sql;
    }

    public String getCurrentSql() {
        return currentSql.get();
    }

    public void clearCurrentSql() {
        currentSql.remove();
    }
}