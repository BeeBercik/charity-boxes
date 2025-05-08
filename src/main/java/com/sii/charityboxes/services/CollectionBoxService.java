package com.sii.charityBoxes.services;

import com.sii.charityBoxes.dto.CollectionBoxRequest;
import com.sii.charityBoxes.dto.CollectionBoxResponse;
import com.sii.charityBoxes.model.CollectionBox;
import com.sii.charityBoxes.model.Currency;
import com.sii.charityBoxes.repositories.CollectionBoxesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CollectionBoxService {

    private final CollectionBoxesRepository boxRepository;

    @Autowired
    public CollectionBoxService(CollectionBoxesRepository boxRepository) {
        this.boxRepository = boxRepository;
    }

    public void registerCollectionBox(CollectionBoxRequest boxRequest) {
        HashMap<Currency, BigDecimal> currencies = new HashMap<>();
        for(Currency c : boxRequest.currencies()) {
            currencies.put(c, BigDecimal.ZERO);
        }

        this.boxRepository.save(new CollectionBox(
                null,
                currencies
        ));
    }

    public List<CollectionBoxResponse> getCollectionBoxes() {
        return this.boxRepository.findAll().stream()
                .map(this::toResponse)
                .toList();

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
