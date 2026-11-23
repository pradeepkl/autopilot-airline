package com.autopilot.airline.booking;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateBookingRequest(
        @NotBlank String passengerName,
        @NotEmpty @Valid List<Item> items) {

    public record Item(
            @NotNull BookingItemType type,
            @NotBlank String description,
            String flightNumber,
            LocalDate departureDate,
            @Min(1) int quantity,
            @NotNull BigDecimal unitPrice) {
    }
}
