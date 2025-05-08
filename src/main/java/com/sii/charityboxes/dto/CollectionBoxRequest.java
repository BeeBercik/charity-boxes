package com.sii.charityBoxes.dto;

import com.sii.charityBoxes.model.Currency;

import java.util.List;

public record CollectionBoxRequest(List<Currency> currencies) {
}
