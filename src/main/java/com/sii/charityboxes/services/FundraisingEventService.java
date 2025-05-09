package com.sii.charityBoxes.services;

import com.sii.charityBoxes.dto.FundraisingEventRequest;
import com.sii.charityBoxes.dto.FundraisingEventResponse;
import com.sii.charityBoxes.exceptions.InvalidCurrencyException;
import com.sii.charityBoxes.model.Currency;
import com.sii.charityBoxes.model.FundraisingEvent;
import com.sii.charityBoxes.repositories.FundraisingEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static java.util.stream.Collectors.toList;

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

    public List<FundraisingEventResponse> getFinancialReport() {
        return this.eventRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public FundraisingEventResponse toResponse(FundraisingEvent event) {
        return new FundraisingEventResponse(
                event.getName(),
                event.getCurrency(),
                event.getAccount()
        );
    }

    public FundraisingEvent fromRequestToEntity(FundraisingEventRequest eventRequest) {
        Currency currency;
        try {
            currency = Currency.valueOf(eventRequest.currency());
        } catch (IllegalArgumentException e) {
            throw new InvalidCurrencyException(eventRequest.currency() + " currency not supported");
        }

        return new FundraisingEvent(
                eventRequest.name(),
                currency,
                BigDecimal.ZERO
        );
    }
}
