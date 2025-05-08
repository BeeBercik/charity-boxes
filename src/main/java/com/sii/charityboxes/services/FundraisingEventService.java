package com.sii.charityBoxes.services;

import com.sii.charityBoxes.dto.FundraisingEventRequest;
import com.sii.charityBoxes.model.FundraisingEvent;
import com.sii.charityBoxes.repositories.FundraisingEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class FundraisingEventService {

    private final FundraisingEventRepository eventRepository;

    @Autowired
    public FundraisingEventService(FundraisingEventRepository fundraisingEventRepository) {
        this.eventRepository = fundraisingEventRepository;
    }

    public void createEvent(FundraisingEventRequest eventRequest) {
        this.eventRepository.save(
                this.fromRequestToEntity(eventRequest)
        );
    }

    public FundraisingEvent fromRequestToEntity(FundraisingEventRequest eventRequest) {
        return new FundraisingEvent(
                eventRequest.name(),
                eventRequest.currency(),
                BigDecimal.ZERO
        );
    }
}
