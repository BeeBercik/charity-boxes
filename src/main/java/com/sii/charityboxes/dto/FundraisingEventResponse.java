package com.sii.charityBoxes.dto;

import com.sii.charityBoxes.model.Currency;

import java.math.BigDecimal;

public record FundraisingEventResponse(String name, Currency currency, BigDecimal amount) {
}
