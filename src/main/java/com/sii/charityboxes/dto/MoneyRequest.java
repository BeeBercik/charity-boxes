package com.sii.charityBoxes.dto;

import com.sii.charityBoxes.model.Currency;

import java.math.BigDecimal;

public record MoneyRequest(Currency currency, BigDecimal money) {
}
