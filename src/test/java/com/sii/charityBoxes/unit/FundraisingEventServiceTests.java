package com.sii.charityBoxes.unit;


import com.sii.charityBoxes.dto.FundraisingEventRequest;
import com.sii.charityBoxes.dto.FundraisingEventResponse;
import com.sii.charityBoxes.exceptions.InvalidCurrencyException;
import com.sii.charityBoxes.model.Currency;
import com.sii.charityBoxes.model.FundraisingEvent;
import com.sii.charityBoxes.repositories.FundraisingEventRepository;
import com.sii.charityBoxes.services.FundraisingEventService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FundraisingEventServiceTests {

    @Mock
    private FundraisingEventRepository eventRepository;

    @InjectMocks
    private FundraisingEventService eventService;

    @Test
    public void testCreateEvent_ifInvalidCurrency() {
        FundraisingEventRequest request = new FundraisingEventRequest("Invalid Event", "XYZ");

        assertThrows(InvalidCurrencyException.class, () -> this.eventService.createEvent(request));
    }

    @Test
    public void testGetFinancialReport_ifEventsNotFound() {
        when(this.eventRepository.findAll()).thenReturn(List.of());

        List<FundraisingEventResponse> resultList = eventService.getFinancialReport();
        assertEquals(resultList, List.of());
    }

    @Test
    public void testGetFinancialReport_ifEventsFound() {
        when(this.eventRepository.findAll()).thenReturn(List.of(
                new FundraisingEvent("name", Currency.EUR, BigDecimal.valueOf(100)),
                new FundraisingEvent("xxx", Currency.GBP, BigDecimal.valueOf(50))
        ));

        assertEquals(2, this.eventService.getFinancialReport().size());
        assertEquals("name", this.eventService.getFinancialReport().get(0).name());
        assertEquals(Currency.EUR, this.eventService.getFinancialReport().get(0).currency());
        assertEquals(BigDecimal.valueOf(100), this.eventService.getFinancialReport().get(0).amount());
    }
}