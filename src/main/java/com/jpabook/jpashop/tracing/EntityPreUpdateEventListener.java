package com.jpabook.jpashop.tracing;

import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@ConditionalOnProperty(name = "apitracing.enabled", havingValue = "true")
public class EntityPreUpdateEventListener implements PreUpdateEventListener {

    Logger logger = Logger.getLogger(EntityPreUpdateEventListener.class.getName());
    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        Object entity = event.getEntity();
        logger.log(Level.INFO, "Previous entity: " + entity);
        Object[] newState = event.getState(); // New entity properties
        Object[] oldState = event.getOldState(); // Old entity properties
        logger.log(Level.INFO, "State transition, " + oldState + " -> " + newState);
        return false;
    }
}
