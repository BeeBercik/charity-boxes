package com.sii.charityBoxes.controllers;

import com.sii.charityBoxes.dto.CollectionBoxRequest;
import com.sii.charityBoxes.dto.CollectionBoxResponse;
import com.sii.charityBoxes.services.CollectionBoxService;
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
    public ResponseEntity<?> registerCollectionBox(@RequestBody CollectionBoxRequest boxRequest) {
        this.boxService.registerCollectionBox(boxRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<CollectionBoxResponse>> listCollectionBoxes() {
        return ResponseEntity.status(HttpStatus.FOUND).body(this.boxService.getCollectionBoxes());
    }
}
