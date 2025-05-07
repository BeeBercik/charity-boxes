package com.sii.charityBoxes.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "collection_boxes")
public class CollectionBox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private FundraisingEvent event;

    @ElementCollection
    @CollectionTable(name = "box_amounts", joinColumns = @JoinColumn(name = "box_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "currency")
    @Column(name = "amount")
    private Map<Currency, BigDecimal> amount = new HashMap<>();

    public CollectionBox() {
    }

    public CollectionBox(FundraisingEvent event, Map<Currency, BigDecimal> amount) {
        this.event = event;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FundraisingEvent getEvent() {
        return event;
    }

    public void setEvent(FundraisingEvent event) {
        this.event = event;
    }

    public Map<Currency, BigDecimal> getAmount() {
        return amount;
    }

    public void setAmount(Map<Currency, BigDecimal> amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "CollectionBox{" +
                "id=" + id +
                ", event=" + event +
                ", amount=" + amount +
                '}';
    }
}
