package com.autopilot.airline.booking;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/** Seeds three sample bookings so every demo step has JSON to look at. */
@Component
class BookingSeeder implements ApplicationRunner {

    private final BookingRepository bookings;

    BookingSeeder(BookingRepository bookings) {
        this.bookings = bookings;
    }

    @Override
    public void run(ApplicationArguments args) {
        Booking oneWay = new Booking("Amelia Earhart", LocalDate.of(2026, 11, 1), "CONFIRMED");
        oneWay.addItem(segment("AP101", LocalDate.of(2026, 11, 22), "Bengaluru to Frankfurt", "480.00"));

        Booking roundTrip = new Booking("Charles Lindbergh", LocalDate.of(2026, 11, 3), "CONFIRMED");
        roundTrip.addItem(segment("AP202", LocalDate.of(2026, 12, 5), "Frankfurt to New York", "620.00"));
        roundTrip.addItem(segment("AP203", LocalDate.of(2026, 12, 19), "New York to Frankfurt", "590.00"));
        roundTrip.addItem(extra(BookingItemType.BAGGAGE, "Extra checked bag 23kg", 2, "60.00"));

        Booking upgrade = new Booking("Bessie Coleman", LocalDate.of(2026, 11, 10), "PENDING");
        upgrade.addItem(segment("AP310", LocalDate.of(2027, 1, 14), "Frankfurt to Singapore", "710.00"));
        upgrade.addItem(extra(BookingItemType.SEAT, "Seat 12A, extra legroom", 1, "35.00"));
        upgrade.addItem(extra(BookingItemType.MEAL, "Vegetarian meal", 1, "0.00"));

        bookings.saveAll(java.util.List.of(oneWay, roundTrip, upgrade));
    }

    private static BookingItem segment(String flight, LocalDate date, String description, String price) {
        return new BookingItem(BookingItemType.SEGMENT, description, flight, date, 1, new BigDecimal(price));
    }

    private static BookingItem extra(BookingItemType type, String description, int quantity, String price) {
        return new BookingItem(type, description, null, null, quantity, new BigDecimal(price));
    }
}
