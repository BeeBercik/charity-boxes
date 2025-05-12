package com.sii.charityBoxes.dto;

import com.sii.charityBoxes.model.Currency;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record MoneyRequest(
        @NotNull(message = "Currency must be provided")
        Currency currency,

        @NotNull(message = "Money amount is required")
        @DecimalMin(value = "0.00", inclusive = false, message = "Amount must be greater than zero")
        BigDecimal money) {
}
