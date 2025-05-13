package com.sii.charityBoxes.integration;

import com.sii.charityBoxes.dto.FundraisingEventRequest;
import com.sii.charityBoxes.services.FundraisingEventService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class FundraisingEventControllerIntegrationTests {

    private MockMvc mockMvc;
    private final FundraisingEventService eventService;

    @Autowired
    public FundraisingEventControllerIntegrationTests(FundraisingEventService eventService, MockMvc mockMvc) {
        this.eventService = eventService;
        this.mockMvc = mockMvc;
    }

    @Test
    public void testCreateEvent_IfTooShortName() throws Exception {
        String json = """
                {
                    "name": "xxx",
                    "currency": "PLN"
                }
                """;

        this.mockMvc.perform(post("/fundraising-events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateEvent_IfCurrencyIsNull() throws Exception {
        String json = """
                {
                    "name": "xxxxxxxxx"
                }
                """;

        this.mockMvc.perform(post("/fundraising-events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateEvent_IfNameIsNull() throws Exception {
        String json = """
                {
                    "currency": "PLN"
                }
                """;

        this.mockMvc.perform(post("/fundraising-events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateEvent_IfSuccess() throws Exception {
        String json = """
                {
                    "name": "xxxxxxxxx",
                    "currency": "PLN"
                }
                """;

        this.mockMvc.perform(post("/fundraising-events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    public void testGetFinancialReport_IfEmptyList() throws Exception {
        this.mockMvc.perform(get("/fundraising-events/report"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void testGetFinancialReport_IfEventsFound() throws Exception {
        this.eventService.createEvent(new FundraisingEventRequest("name1", "PLN"));
        this.eventService.createEvent(new FundraisingEventRequest("xxxxx", "GBP"));

        this.mockMvc.perform(get("/fundraising-events/report"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("name1"));
    }
}
