package com.sii.charityBoxes.dto;

import com.sii.charityBoxes.model.Currency;

public record FundraisingEventRequest(String name, Currency currency) {
}
