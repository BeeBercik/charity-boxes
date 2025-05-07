package com.sii.charityBoxes.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "fundraising_events")
public class FundraisingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Currency currency;
    private BigDecimal account;

    public FundraisingEvent() {
    }

    public FundraisingEvent(Currency currency, BigDecimal account) {
        this.currency = currency;
        this.account = account;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAccount() {
        return account;
    }

    public void setAccount(BigDecimal account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "FundraisingEvent{" +
                "id=" + id +
                ", currency=" + currency +
                ", account=" + account +
                '}';
    }
}
