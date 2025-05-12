package com.sii.charityBoxes.unit;

import com.sii.charityBoxes.dto.CollectionBoxRequest;
import com.sii.charityBoxes.dto.CollectionBoxResponse;
import com.sii.charityBoxes.dto.MoneyRequest;
import com.sii.charityBoxes.exceptions.CollectionBoxNotFoundException;
import com.sii.charityBoxes.exceptions.FundraisingEventNotFound;
import com.sii.charityBoxes.exceptions.InvalidCurrencyException;
import com.sii.charityBoxes.model.CollectionBox;
import com.sii.charityBoxes.model.Currency;
import com.sii.charityBoxes.model.FundraisingEvent;
import com.sii.charityBoxes.repositories.CollectionBoxesRepository;
import com.sii.charityBoxes.repositories.FundraisingEventRepository;
import com.sii.charityBoxes.services.CollectionBoxService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CollectionBoxServiceTests {

    @Mock
    private CollectionBoxesRepository boxRepository;

    @Mock
    private FundraisingEventRepository eventRepository;

    @InjectMocks
    private CollectionBoxService boxService;

    @Test
    public void testRegisterBox_IfInvalidCurrency() {
        CollectionBoxRequest boxRequest = new CollectionBoxRequest(List.of("GBP", "YEN"));

        assertThrows(InvalidCurrencyException.class, () -> this.boxService.registerBox(boxRequest));
    }

    @Test
    public void testRegisterBox_IfSuccess() {
        CollectionBoxRequest boxRequest = new CollectionBoxRequest(List.of("GBP"));

        this.boxService.registerBox(boxRequest);

        verify(boxRepository).save(any(CollectionBox.class));
        verifyNoInteractions(this.eventRepository);
    }

    @Test
    public void testGetAllBoxes_IfSuccess() {
        FundraisingEvent event = new FundraisingEvent("name", Currency.PLN, BigDecimal.valueOf(100));
        Map<Currency, BigDecimal> amount = new HashMap<>();
        CollectionBox box = new CollectionBox(event, amount);

        when(this.boxRepository.findAll()).thenReturn(List.of(box));

        List<CollectionBoxResponse> result = this.boxService.getAllBoxes();
        assertEquals(1, result.size());
        assertTrue( result.get(0).isAssigned());
        assertTrue(result.get(0).isEmpty());

        verifyNoInteractions(this.eventRepository);
    }

    @Test
    public void testUnregisterBox_IfBoxNotFound() {
        when(this.boxRepository.findById(1L)).thenThrow(new CollectionBoxNotFoundException("msg"));

        assertThrows(CollectionBoxNotFoundException.class, () -> this.boxService.unregisterBox(1L));
    }

    @Test
    public void testUnregisterBox_IfSuccess() {
        CollectionBox box = new CollectionBox();
        box.setId(1L);

        when(this.boxRepository.findById(1L)).thenReturn(Optional.of(box));
        this.boxService.unregisterBox(1L);

        verify(this.boxRepository).delete(box);
    }

    @Test
    public void testAssignBoxToEvent_IfBoxNotFound() {
        assertThrows(CollectionBoxNotFoundException.class, () -> this.boxService.assignBoxToEvent(1L, 1L));
    }

    @Test
    public void testAssignBoxToEvent_IfEventNotFound() {
        CollectionBox box = new CollectionBox();
        box.setId(1L);

        when(this.boxRepository.findById(1L)).thenReturn(Optional.of(box));

        assertThrows(FundraisingEventNotFound.class, () -> this.boxService.assignBoxToEvent(1L, 1L));
    }

    @Test
    public void testAssignBoxToEvent_IfCollectionBoxNotEmpty() {
        FundraisingEvent event = new FundraisingEvent("name", Currency.GBP, BigDecimal.ZERO);
        CollectionBox box = new CollectionBox(
                event,
                Map.of(Currency.EUR, BigDecimal.valueOf(50))
        );
        box.setId(1L);

        when(this.boxRepository.findById(1L)).thenReturn(Optional.of(box));
        when(this.eventRepository.findById(1L)).thenReturn(Optional.of(event));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> this.boxService.assignBoxToEvent(1L, 1L));

        assertEquals("Cannot assign not empty collection box", ex.getMessage());
    }

    @Test
    public void testAssignBoxToEvent_IfCollectionBoxAlreadyAssigned() {
        FundraisingEvent event = new FundraisingEvent("name", Currency.GBP, BigDecimal.ZERO);
        CollectionBox box = new CollectionBox(
                event,
                Map.of()
        );
        box.setId(1L);

        when(this.boxRepository.findById(1L)).thenReturn(Optional.of(box));
        when(this.eventRepository.findById(1L)).thenReturn(Optional.of(event));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> this.boxService.assignBoxToEvent(1L, 1L));

        assertEquals("Collection box is already assigned to fundraising event", ex.getMessage());
    }

    @Test
    public void testPutMoneyInsideBox_IfCollectionBoxNotFound() {
        when(this.boxRepository.findById(1L)).thenThrow(new CollectionBoxNotFoundException("msg"));

        assertThrows(CollectionBoxNotFoundException.class, () -> this.boxService.putMoneyInsideBox(1L, new MoneyRequest(Currency.GBP, BigDecimal.ZERO)));
    }

    @Test
    public void testPutMoneyInsideBox_IfSuccess() {
        Map<Currency, BigDecimal> map = new HashMap<>();
        CollectionBox box = new CollectionBox(null, map);
        box.setId(1L);

        when(this.boxRepository.findById(1L)).thenReturn(Optional.of(box));

        this.boxService.putMoneyInsideBox(1L, new MoneyRequest(Currency.PLN, BigDecimal.valueOf(100)));

        verify(this.boxRepository).save(box);
    }

    @Test
    public void testPutMoneyInsideBox_IfPuttingMoneyInExistingCurrencyCalculatesCorrect() {
        Map<Currency, BigDecimal> amount = new HashMap<>();
        amount.put(Currency.PLN, BigDecimal.valueOf(100));
        CollectionBox box = new CollectionBox(null, amount);
        box.setId(1L);

        when(this.boxRepository.findById(1L)).thenReturn(Optional.of(box));

        this.boxService.putMoneyInsideBox(1L, new MoneyRequest(Currency.PLN, BigDecimal.valueOf(100)));

        ArgumentCaptor<CollectionBox> captor = ArgumentCaptor.forClass(CollectionBox.class);
        verify(this.boxRepository).save(captor.capture());
        CollectionBox saved = captor.getValue();

        assertEquals(BigDecimal.valueOf(200), saved.getAmount().get(Currency.PLN));
    }

    @Test
    public void testPutMoneyInsideBox_IfNewCurrencyIsSummingCorrect() {
        Map<Currency, BigDecimal> map = new HashMap<>();
        map.put(Currency.PLN, BigDecimal.valueOf(100));
        CollectionBox box = new CollectionBox(null, map);
        box.setId(1L);

        when(this.boxRepository.findById(1L)).thenReturn(Optional.of(box));

        this.boxService.putMoneyInsideBox(1L, new MoneyRequest(Currency.EUR, BigDecimal.valueOf(50)));

        ArgumentCaptor<CollectionBox> captor = ArgumentCaptor.forClass(CollectionBox.class);
        verify(this.boxRepository).save(captor.capture());

        CollectionBox saved = captor.getValue();
        assertEquals(2, saved.getAmount().size());
        assertEquals(BigDecimal.valueOf(100), saved.getAmount().get(Currency.PLN));
        assertEquals(BigDecimal.valueOf(50), saved.getAmount().get(Currency.EUR));
    }

    @Test
    public void testTransferBoxMoney_IfCollectionBoxNotFound() {
        when(this.boxRepository.findById(1L)).thenThrow(new CollectionBoxNotFoundException("msg"));

        assertThrows(CollectionBoxNotFoundException.class, () -> this.boxService.transferBoxMoney(1L));
    }

    @Test
    public void testTransferBoxMoney_IfCollectionBoxNotAssigned() {
        when(this.boxRepository.findById(1L)).thenReturn(Optional.of(
                new CollectionBox(null, Map.of())
        ));

        assertThrows(FundraisingEventNotFound.class, () -> this.boxService.transferBoxMoney(1L));
    }

    @Test
    public void testTransferBoxMoney_IfEmptyBox() {
        CollectionBox box =  new CollectionBox(
                new FundraisingEvent("name", Currency.EUR, BigDecimal.ZERO),
                Map.of());

        when(this.boxRepository.findById(1L)).thenReturn(Optional.of(box));

        assertThrows(IllegalStateException.class, () -> this.boxService.transferBoxMoney(1L));
    }

    @Test
    public void testTransferBoxMoney_IfSuccess() {
        Map<Currency, BigDecimal> map = new HashMap<>();
        map.put(Currency.PLN, BigDecimal.valueOf(100));
        CollectionBox box =  new CollectionBox(
                new FundraisingEvent("name", Currency.EUR, BigDecimal.ZERO),
                map);

        when(this.boxRepository.findById(1L)).thenReturn(Optional.of(box));

        this.boxService.transferBoxMoney(1L);

        verify(this.eventRepository).save(box.getEvent());
        verify(this.boxRepository).save(box);
    }
}
