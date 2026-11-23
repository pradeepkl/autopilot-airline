package com.autopilot.airline.booking;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record BookingResponse(
        Long id,
        String passengerName,
        LocalDate bookingDate,
        String status,
        List<ItemResponse> items) {

    public record ItemResponse(
            Long id,
            BookingItemType type,
            String description,
            String flightNumber,
            LocalDate departureDate,
            int quantity,
            BigDecimal unitPrice) {

        static ItemResponse from(BookingItem item) {
            return new ItemResponse(item.getId(), item.getType(), item.getDescription(),
                    item.getFlightNumber(), item.getDepartureDate(), item.getQuantity(),
                    item.getUnitPrice());
        }
    }

    static BookingResponse from(Booking booking) {
        return new BookingResponse(booking.getId(), booking.getPassengerName(),
                booking.getBookingDate(), booking.getStatus(),
                booking.getItems().stream().map(ItemResponse::from).toList());
    }
}
