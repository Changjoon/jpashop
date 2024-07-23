package com.jpabook.jpashop.tracing;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class CustomInterceptor extends EmptyInterceptor {

    // INSERT 작업 시 호출되는 메서드
    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        System.out.println("!!!! Entity inserted: " + entity.getClass().getName());
        System.out.println("ID: " + id);
        for (int i = 0; i < propertyNames.length; i++) {
            System.out.println(propertyNames[i] + ": " + state[i]);
        }
        return super.onSave(entity, id, state, propertyNames, types);
    }

    // UPDATE 작업 시 호출되는 메서드
    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        if (previousState != null) {
            System.out.println("!!!! Entity updated: " + entity.getClass().getName());
            System.out.println("ID: " + id);
            System.out.println("Properties: " + propertyNames.length);
            for (int i = 0; i < propertyNames.length; i++) {
                System.out.println(propertyNames[i] + ": " + previousState[i] + " -> " + currentState[i]);
            }
        }
        return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    }

    // DELETE 작업 시 호출되는 메서드
    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        System.out.println("!!!! Entity deleted: " + entity.getClass().getName());
        System.out.println("ID: " + id);
        for (int i = 0; i < propertyNames.length; i++) {
            System.out.println(propertyNames[i] + ": " + state[i]);
        }
        super.onDelete(entity, id, state, propertyNames, types);
    }

    // SELECT 작업 시 호출되는 메서드
    @Override
    public Object getEntity(String entityName, Serializable id) {
        Object entity = super.getEntity(entityName, id);
        if (entity != null) {
            System.out.println("!!!! Entity selected: " + entity.getClass().getName());
            System.out.println("ID: " + id);
        }
        return entity;
    }
}
