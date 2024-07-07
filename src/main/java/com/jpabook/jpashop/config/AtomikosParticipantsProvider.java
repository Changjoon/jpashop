package com.jpabook.jpashop.config;

import com.atomikos.remoting.twopc.ParticipantsProvider;
import org.springframework.stereotype.Component;

@Component
public class AtomikosParticipantsProvider extends ParticipantsProvider {

    private final ParticipantsProvider participantsProvider;

    public AtomikosParticipantsProvider(ParticipantsProvider participantsProvider) {
        this.participantsProvider = participantsProvider;
    }
}
