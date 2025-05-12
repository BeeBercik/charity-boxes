package com.sii.charityBoxes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FundraisingEventRequest(
        @NotBlank(message = "Name must not be blank")
        @Size(min = 5, message = "Event name too short")
        String name,

        @NotNull(message = "Currency is required")
        String currency) {
}
