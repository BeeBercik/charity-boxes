package com.sii.charityBoxes.controllers;

import com.sii.charityBoxes.dto.FundraisingEventRequest;
import com.sii.charityBoxes.services.FundraisingEventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fundraising-events")
public class FundraisingController {

    public final FundraisingEventService eventService;

    public FundraisingController(FundraisingEventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody FundraisingEventRequest event) {
        this.eventService.createEvent(event);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
