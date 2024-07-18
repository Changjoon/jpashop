package com.jpabook.jpashop.config;

import com.atomikos.remoting.twopc.AtomikosRestPort;
import com.atomikos.remoting.twopc.ParticipantsProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletProperties;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(JacksonJsonProvider.class);
        register(ParticipantsProvider.class);
        register(AtomikosRestPort.class);
        property(ServletProperties.FILTER_FORWARD_ON_404, true);
        property(ServletProperties.FILTER_STATIC_CONTENT_REGEX, "/members/.*");
    }
}
