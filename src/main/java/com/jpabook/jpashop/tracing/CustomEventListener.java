package com.jpabook.jpashop.tracing;

import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.event.spi.
import org.springframework.stereotype.Component;

@Component
public class CustomEventListener implements PreInsertEventListener, PreUpdateEventListener {

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        System.out.println("Pre Insert: " + event.getEntity().getClass().getName());
        for (int i = 0; i < event.getState().length; i++) {
            System.out.println(event.getPersister().getPropertyNames()[i] + ": " + event.getState()[i]);
        }
        return false;
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        System.out.println("Pre Update: " + event.getEntity().getClass().getName());
        for (int i = 0; i < event.getState().length; i++) {
            System.out.println(event.getPersister().getPropertyNames()[i] + ": " + event.getOldState()[i] + " -> " + event.getState()[i]);
        }
        return false;
    }
}
