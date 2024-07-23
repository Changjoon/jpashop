package com.jpabook.jpashop.tracing;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.sql.PreparedStatement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;


@Aspect
@Configuration
public class PreparedStatementLoggingAspect {

    @Autowired
    private CustomStatementInspector customStatementInspector;

    @Around("execution(* java.sql.PreparedStatement.execute*(..))")
    public Object aroundExecute(ProceedingJoinPoint pjp) throws Throwable {
        Object result = pjp.proceed();

        Object target = pjp.getTarget();
        if (target instanceof PreparedStatement) {
            PreparedStatement preparedStatement = (PreparedStatement) target;
            String sql = customStatementInspector.getCurrentSql();
            if (sql != null) {
                String boundSql = bindParameters(sql, preparedStatement);
                System.out.println("Executing SQL with values: " + boundSql);
                customStatementInspector.clearCurrentSql();
            }
        }

        return result;
    }

    private String bindParameters(String sql, PreparedStatement statement) throws Exception {
        Matcher matcher = Pattern.compile("\\?").matcher(sql);
        StringBuffer sb = new StringBuffer();
        int index = 1;
        while (matcher.find()) {
            matcher.appendReplacement(sb, getParameterValue(statement, index++));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String getParameterValue(PreparedStatement statement, int index) throws Exception {
        Object paramValue = statement.getParameterMetaData().getParameterTypeName(index);
        return paramValue == null ? "null" : paramValue.toString();
    }
}