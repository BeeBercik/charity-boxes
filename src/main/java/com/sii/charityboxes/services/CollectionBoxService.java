package com.sii.charityBoxes.services;

import com.sii.charityBoxes.dto.CollectionBoxRequest;
import com.sii.charityBoxes.dto.CollectionBoxResponse;
import com.sii.charityBoxes.dto.MoneyRequest;
import com.sii.charityBoxes.exceptions.CollectionBoxNotFoundException;
import com.sii.charityBoxes.exceptions.FundraisingEventNotFound;
import com.sii.charityBoxes.model.CollectionBox;
import com.sii.charityBoxes.model.Currency;
import com.sii.charityBoxes.model.FundraisingEvent;
import com.sii.charityBoxes.repositories.CollectionBoxesRepository;
import com.sii.charityBoxes.repositories.FundraisingEventRepository;
import org.apache.juli.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CollectionBoxService {

    private final CollectionBoxesRepository boxRepository;
    private final FundraisingEventRepository eventRepository;

    @Autowired
    public CollectionBoxService(CollectionBoxesRepository boxRepository, FundraisingEventRepository eventRepository) {
        this.boxRepository = boxRepository;
        this.eventRepository = eventRepository;
    }

    public void registerBox(CollectionBoxRequest boxRequest) {
        HashMap<Currency, BigDecimal> currencies = new HashMap<>();
        for(Currency c : boxRequest.currencies()) {
            currencies.put(c, BigDecimal.ZERO);
        }

        this.boxRepository.save(new CollectionBox(
                null,
                currencies
        ));
    }

    public List<CollectionBoxResponse> getAllBoxes() {
        return this.boxRepository.findAll().stream()
                .map(this::toResponse)
                .toList();

    }

    public void unregisterBox(Long id) {
        CollectionBox box = this.boxRepository.findById(id).orElseThrow(() -> new CollectionBoxNotFoundException("Collection Box not found"));

        this.boxRepository.delete(box);
    }

    public void assignBoxToEvent(Long boxId, Long eventId) {
        CollectionBox box = this.boxRepository.findById(boxId).orElseThrow(() -> new CollectionBoxNotFoundException("Collection Box not found"));

        FundraisingEvent event = this.eventRepository.findById(eventId).orElseThrow(() -> new FundraisingEventNotFound("Fundraising event not found"));

        if(this.sumBoxAmount(box).compareTo(BigDecimal.ZERO) != 0)  {
            throw new IllegalStateException("Cannot assign not empty collection box");
        }
        if(box.getEvent() != null) {
            throw new IllegalStateException("Collection box is already assigned to fundraising event");
        }

        box.setEvent(event);
        this.boxRepository.save(box);
    }

    public void putMoneyInsideBox(Long boxId, MoneyRequest moneyRequest) {
        CollectionBox box = this.boxRepository.findById(boxId).orElseThrow(() -> new CollectionBoxNotFoundException("Collection Box not found"));

        box.getAmount().merge(
                moneyRequest.currency(),
                moneyRequest.money(),
                (oldVal, newVal) -> oldVal.add(newVal));

        this.boxRepository.save(box);
    }

    public BigDecimal sumBoxAmount(CollectionBox box) {
        BigDecimal total = BigDecimal.ZERO;

        for(Map.Entry<Currency, BigDecimal> pair : box.getAmount().entrySet()) {
            Currency currency = pair.getKey();
            BigDecimal value = pair.getValue();
            total = currency.getRate().multiply(value);
        }

        return total;
    }

    public CollectionBoxResponse toResponse(CollectionBox box) {
        return new CollectionBoxResponse(
                        box.getId(),
                        box.getEvent() != null,
                this.sumBoxAmount(box).compareTo(BigDecimal.ZERO) == 0);
    }
}
