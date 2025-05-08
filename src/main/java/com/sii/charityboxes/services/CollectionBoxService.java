package com.sii.charityBoxes.services;

import com.sii.charityBoxes.dto.CollectionBoxRequest;
import com.sii.charityBoxes.model.CollectionBox;
import com.sii.charityBoxes.model.Currency;
import com.sii.charityBoxes.repositories.CollectionBoxesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;

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
}
