package com.sii.charityBoxes.controllers;

import com.sii.charityBoxes.dto.FundraisingEventRequest;
import com.sii.charityBoxes.dto.FundraisingEventResponse;
import com.sii.charityBoxes.services.FundraisingEventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fundraising-events")
public class FundraisingEventController {

    public final FundraisingEventService eventService;

    public FundraisingEventController(FundraisingEventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<?> createEvent(@Valid @RequestBody FundraisingEventRequest event) {
        this.eventService.createEvent(event);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/report")
    public ResponseEntity<List<FundraisingEventResponse>> getFinancialReport() {
        return ResponseEntity.status(HttpStatus.OK).body(this.eventService.getFinancialReport());
    }
}
