package com.sii.charityBoxes.model;

import java.math.BigDecimal;

public enum Currency {
    PLN("PLN", BigDecimal.valueOf(1.00)),
    EUR("EUR", BigDecimal.valueOf(4.27)),
    GBP("GBP", BigDecimal.valueOf(5.02)),
    USD("USD", BigDecimal.valueOf(3.76));

    private final String name;
    private final BigDecimal rate;

    Currency(String name, BigDecimal rate) {
        this.name = name;
        this.rate = rate;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getRate() {
        return rate;
    }
}
