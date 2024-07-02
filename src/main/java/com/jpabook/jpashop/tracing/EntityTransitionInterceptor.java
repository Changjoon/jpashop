package com.jpabook.jpashop.tracing;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Arrays;
import java.util.logging.Logger;

public class EntityTransitionInterceptor extends EmptyInterceptor {
    Logger logger = Logger.getLogger(EntityTransitionInterceptor.class.getName());
    @Override
    public boolean onSave(Object entity, Serializable id,
                          Object[] state, String[] propertyNames, Type[] types) {

        logger.info("onSave: " + entity.toString());
        logger.info("state: " + Arrays.toString(state));
        logger.info("propertyNames: " + Arrays.toString(propertyNames));
        logger.info("types: " + Arrays.toString(types));

        return super.onSave(entity, id, state, propertyNames, types);
    }
    @Override
    public boolean onFlushDirty(Object entity, Serializable id,
                                Object[] currentState, Object [] previousState,
                                String[] propertyNames, Type[] types) {
        if (previousState != null) {
            for (int i = 0; i < propertyNames.length; i++) {
                if (currentState[i] != null && !currentState[i].equals(previousState[i])) {
                    System.out.println(String.format("onFlushDirty:\n\tEntity: %s, Property: %s, Previous Value: %s, Current Value: %s",
                            entity.getClass().getSimpleName(), propertyNames[i], previousState[i], currentState[i]));
                }
            }
        }

        return super.onFlushDirty(entity, id, currentState,
                previousState, propertyNames, types);
    }
}
