package com.sii.charityBoxes.controllers;

import com.sii.charityBoxes.dto.CollectionBoxRequest;
import com.sii.charityBoxes.dto.CollectionBoxResponse;
import com.sii.charityBoxes.dto.MoneyRequest;
import com.sii.charityBoxes.services.CollectionBoxService;
import jakarta.validation.Valid;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/collection-boxes")
public class CollectionBoxController {

    private final CollectionBoxService boxService;

    @Autowired
    public CollectionBoxController(CollectionBoxService boxService) {
        this.boxService = boxService;
    }

    @PostMapping
    public ResponseEntity<?> registerBox(@Valid @RequestBody CollectionBoxRequest boxRequest) {
        this.boxService.registerBox(boxRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<CollectionBoxResponse>> listBoxes() {
        return ResponseEntity.status(HttpStatus.FOUND).body(this.boxService.getAllBoxes());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> unregisterBox(@PathVariable(name = "id") Long id) {
        this.boxService.unregisterBox(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{boxId}/assignTo/{eventId}")
    public ResponseEntity<?> assignBoxToEvent(@PathVariable(name = "boxId") Long boxId,
                                              @PathVariable(name = "eventId") Long eventId) {
        this.boxService.assignBoxToEvent(boxId, eventId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{id}/putMoney")
    public ResponseEntity<?> putMoneyInsideBox(@PathVariable(name = "id") Long id,
                                               @Valid @RequestBody MoneyRequest moneyRequest) {
        this.boxService.putMoneyInsideBox(id, moneyRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}/transfer")
    public ResponseEntity<?> transferBoxMoney(@PathVariable(name = "id") Long id) {
        this.boxService.transferBoxMoney(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
