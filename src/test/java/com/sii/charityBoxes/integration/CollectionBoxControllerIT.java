package com.sii.charityBoxes.integration;

import com.sii.charityBoxes.exceptions.CollectionBoxNotFoundException;
import com.sii.charityBoxes.exceptions.FundraisingEventNotFound;
import com.sii.charityBoxes.model.CollectionBox;
import com.sii.charityBoxes.model.Currency;
import com.sii.charityBoxes.model.FundraisingEvent;
import com.sii.charityBoxes.repositories.CollectionBoxRepository;
import com.sii.charityBoxes.repositories.FundraisingEventRepository;
import com.sii.charityBoxes.services.CollectionBoxService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CollectionBoxControllerIT {

    private final MockMvc mockMvc;
    private final CollectionBoxRepository boxRepository;
    private final FundraisingEventRepository eventRepository;

    @Autowired
    public CollectionBoxControllerIT(MockMvc mockMvc, CollectionBoxRepository boxRepository, FundraisingEventRepository eventRepository) {
        this.mockMvc = mockMvc;
        this.boxRepository = boxRepository;
        this.eventRepository = eventRepository;
    }

    @Test
    public void testRegisterBox_IfCurrenciesListEmpty() throws Exception {
        String json = """
                {
                    "currencies": []
                }
                """;

        this.mockMvc.perform(post("/collection-boxes")
                        .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegisterBox_IfCurrenciesListIsNull() throws Exception {
        String json = """
                {
                }
                """;

        this.mockMvc.perform(post("/collection-boxes")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRegisterBox_IfSuccess() throws Exception {
        String json = """
                {
                    "currencies": ["EUR", "PLN"]
                }
                """;

        this.mockMvc.perform(post("/collection-boxes")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void testListBoxes_ifEventsFound() throws Exception {
        String box1 = """
                {
                    "currencies": ["EUR", "PLN"]
                }
                """;
        String box2 = """
                {
                    "currencies": ["GBP", "USD"]
                }
                """;
        this.mockMvc.perform(post("/collection-boxes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(box1))
                .andExpect(status().isCreated());
        this.mockMvc.perform(post("/collection-boxes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(box2))
                .andExpect(status().isCreated());

        this.mockMvc.perform(get("/collection-boxes"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void testListBoxes_ifEventsNotFound() throws Exception {
        this.mockMvc.perform(get("/collection-boxes"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void testUnregisterBox_IfBoxDoesntExist() throws Exception {
        this.mockMvc.perform(delete("/collection-boxes/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUnregisterBox_IfSuccess() throws Exception {
        Long id = this.boxRepository.save(new CollectionBox(
                null,
                Map.of()
        )).getId();
        this.mockMvc.perform(delete("/collection-boxes/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testAssignBoxToEvent_IfBoxNotFound() throws Exception {
        this.eventRepository.save(new FundraisingEvent());
        this.mockMvc.perform(put("/collection-boxes/1/assignTo/1"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(CollectionBoxNotFoundException.class, result.getResolvedException()));
    }

    @Test
    public void testAssignBoxToEvent_IfEventNotFound() throws Exception {
        Long id = this.boxRepository.save(new CollectionBox(
                null,
                Map.of(Currency.EUR, BigDecimal.valueOf(10))
        )).getId();
        this.mockMvc.perform(put("/collection-boxes/" + id + "/assignTo/1"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(FundraisingEventNotFound.class, result.getResolvedException()));
    }

    @Test
    public void testAssignBoxToEvent_IfBoxNotEmpty() throws Exception {
        Long boxId = this.boxRepository.save(new CollectionBox(
                null,
                Map.of(Currency.EUR, BigDecimal.valueOf(10))
        )).getId();
        Long eventId = this.eventRepository.save(new FundraisingEvent()).getId();
        this.mockMvc.perform(put("/collection-boxes/" + boxId + "/assignTo/" + eventId))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(IllegalStateException.class, result.getResolvedException()));
    }

    @Test
    public void testAssignBoxToEvent_IfBoxAlreadyAssigned() throws Exception {
        FundraisingEvent event = new FundraisingEvent();
        Long eventId = this.eventRepository.save(event).getId();
        Long boxId = this.boxRepository.save(new CollectionBox(
                event,
                Map.of(Currency.EUR, BigDecimal.valueOf(10))
        )).getId();
        this.mockMvc.perform(put("/collection-boxes/" + boxId + "/assignTo/" + eventId))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(IllegalStateException.class, result.getResolvedException()));;
    }

    @Test
    public void testAssignBoxToEvent_IfSuccess() throws Exception {
        Long boxId = this.boxRepository.save(new CollectionBox(
                null,
                Map.of()
        )).getId();
        Long eventId = this.eventRepository.save(new FundraisingEvent()).getId();
        this.mockMvc.perform(put("/collection-boxes/" + boxId + "/assignTo/" + eventId))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testPutMoneyInsideBox_IfBoxDoesntExist() throws Exception {
        String json =
                """
                {
                    "currency": "PLN",
                    "money": 100
                }
                """;;
        this.mockMvc.perform(post("/collection-boxes/-1/putMoney")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(CollectionBoxNotFoundException.class, result.getResolvedException()));
    }

    @Test
    public void testPutMoneyInsideBox_IfCurrencyIsNull() throws Exception {
        Long id = this.boxRepository.save(new CollectionBox(
                null,
                Map.of()
        )).getId();
        String json =
                """
                {
                    "money": 100
                }
                """;;
        this.mockMvc.perform(post("/collection-boxes/" + id + "/putMoney")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()));
    }

    @Test
    public void testPutMoneyInsideBox_IfMoneyIsNull() throws Exception {
        Long id = this.boxRepository.save(new CollectionBox(
                null,
                Map.of()
        )).getId();
        String json =
                """
                {
                    "currency": "PLN"
                }
                """;;
        this.mockMvc.perform(post("/collection-boxes/" + id + "/putMoney")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()));
    }

    @Test
    public void testPutMoneyInsideBox_IfSuccess() throws Exception {
        Map<Currency, BigDecimal> map = new HashMap<>();
        Long id = this.boxRepository.save(new CollectionBox(null, map)).getId();
        String json =
                """
                {
                    "currency": "PLN",
                    "money": 100
                }
                """;;
        this.mockMvc.perform(post("/collection-boxes/" + id + "/putMoney")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testPutMoneyInsideBox_IfPutMoneyCorrectly() throws Exception {
        Map<Currency, BigDecimal> map = new HashMap<>();
        Long id = this.boxRepository.save(new CollectionBox(null, map)).getId();
        String json =
                """
                {
                    "currency": "PLN",
                    "money": 100
                }
                """;;
        this.mockMvc.perform(post("/collection-boxes/" + id + "/putMoney")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent());

        CollectionBox box = this.boxRepository.findById(id).orElseThrow();
        assertEquals(0,
                BigDecimal.valueOf(100).compareTo(box.getAmount().get(Currency.PLN)));
    }

    @Test
    public void testPutMoneyInsideBox_IfMoneyIsZero() throws Exception {
        Map<Currency, BigDecimal> map = new HashMap<>();
        Long id = this.boxRepository.save(new CollectionBox(null, map)).getId();
        String json =
                """
                {
                    "currency": "PLN",
                    "money": 0
                }
                """;
        this.mockMvc.perform(post("/collection-boxes/" + id + "/putMoney")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()));
    }

    @Test
    public void testTransferBoxMoney_IfBoxDoesntExist() throws Exception {
        this.mockMvc.perform(put("/collection-boxes/-1/transfer"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(CollectionBoxNotFoundException.class, result.getResolvedException()));
    }

    @Test
    public void testTransferBoxMoney_IfBoxNotAssigned() throws Exception {
        Map<Currency, BigDecimal> map = new HashMap<>();
        map.put(Currency.PLN, BigDecimal.valueOf(100));

        Long id = this.boxRepository.save(new CollectionBox(null, map)).getId();
        this.mockMvc.perform(put("/collection-boxes/" + id + "/transfer"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(FundraisingEventNotFound.class, result.getResolvedException()));
    }

    @Test
    public void testTransferBoxMoney_IfEmptyBox() throws Exception {
        FundraisingEvent event = new FundraisingEvent("name", Currency.PLN, BigDecimal.ZERO);

        Map<Currency, BigDecimal> map = new HashMap<>();
        Long id = this.boxRepository.save(new CollectionBox(event, map)).getId();
        this.mockMvc.perform(put("/collection-boxes/" + id + "/transfer"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(IllegalStateException.class, result.getResolvedException()));
    }

    @Test
    public void testTransferBoxMoney_IfSuccess() throws Exception {
        FundraisingEvent event = new FundraisingEvent("name", Currency.PLN, BigDecimal.ZERO);

        Map<Currency, BigDecimal> map = new HashMap<>();
        map.put(Currency.PLN, BigDecimal.valueOf(100));

        Long id = this.boxRepository.save(new CollectionBox(event, map)).getId();
        this.mockMvc.perform(put("/collection-boxes/" + id + "/transfer"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testTransferBoxMoney_IfTransferMoneyCorrectly() throws Exception {
        FundraisingEvent event = new FundraisingEvent("name", Currency.PLN, BigDecimal.ZERO);

        Map<Currency, BigDecimal> map = new HashMap<>();
        map.put(Currency.PLN, BigDecimal.valueOf(100));

        Long id = this.boxRepository.save(new CollectionBox(event, map)).getId();
        this.mockMvc.perform(put("/collection-boxes/" + id + "/transfer"))
                .andExpect(status().isNoContent());

        CollectionBox box = this.boxRepository.findById(id).orElseThrow();
        assertEquals(0,
                BigDecimal.valueOf(100).compareTo(box.getEvent().getAccount()));
    }
}
