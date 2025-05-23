package com.sii.charityBoxes.services;

import com.sii.charityBoxes.dto.CollectionBoxRequest;
import com.sii.charityBoxes.dto.CollectionBoxResponse;
import com.sii.charityBoxes.dto.MoneyRequest;
import com.sii.charityBoxes.exceptions.CollectionBoxNotFoundException;
import com.sii.charityBoxes.exceptions.FundraisingEventNotFound;
import com.sii.charityBoxes.exceptions.InvalidCurrencyException;
import com.sii.charityBoxes.model.CollectionBox;
import com.sii.charityBoxes.model.Currency;
import com.sii.charityBoxes.model.FundraisingEvent;
import com.sii.charityBoxes.repositories.CollectionBoxRepository;
import com.sii.charityBoxes.repositories.FundraisingEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CollectionBoxService {

    private final CollectionBoxRepository boxRepository;
    private final FundraisingEventRepository eventRepository;

    @Autowired
    public CollectionBoxService(CollectionBoxRepository boxRepository, FundraisingEventRepository eventRepository) {
        this.boxRepository = boxRepository;
        this.eventRepository = eventRepository;
    }

    public void registerBox(CollectionBoxRequest boxRequest) {
        HashMap<Currency, BigDecimal> currencies = new HashMap<>();
        for(String c : boxRequest.currencies()) {
            Currency currency;
            try {
                 currency = Currency.valueOf(c);
            } catch (IllegalArgumentException e) {
                throw new InvalidCurrencyException(c + " currency not supported");
            }
            currencies.put(currency, BigDecimal.ZERO);
        }

        this.boxRepository.save(new CollectionBox(null, currencies));
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

    public void transferBoxMoney(Long boxId) {
        CollectionBox box = this.boxRepository.findById(boxId).orElseThrow(() -> new CollectionBoxNotFoundException("Collection Box not found"));

        if(box.getEvent() == null) {
            throw new FundraisingEventNotFound("Collection box is not assigned to any fundraising event");
        }

        BigDecimal sum = this.sumBoxAmount(box);
        if(sum.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalStateException("Cannot transfer money from empty box");
        }
        box.getEvent().setAccount(
                box.getEvent().getAccount().add(sum));
        box.getAmount().clear();

        this.eventRepository.save(box.getEvent());
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
