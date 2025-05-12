package com.sii.charityBoxes.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CollectionBoxRequest(
        @NotNull(message = "List of currencies is required")
        @Size(min = 1, message = "List must have at least one currency")
        List<String> currencies) {
}
