package com.autopilot.airline.booking;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/bookings")
class BookingController {

    private final BookingRepository bookings;

    BookingController(BookingRepository bookings) {
        this.bookings = bookings;
    }

    @GetMapping
    List<BookingResponse> list() {
        return bookings.findAll().stream().map(BookingResponse::from).toList();
    }

    @GetMapping("/{id}")
    BookingResponse get(@PathVariable Long id) {
        return bookings.findById(id)
                .map(BookingResponse::from)
                .orElseThrow(() -> new BookingNotFoundException(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    BookingResponse create(@Valid @RequestBody CreateBookingRequest request) {
        Booking booking = new Booking(request.passengerName(), LocalDate.now(), "PENDING");
        request.items().forEach(i -> booking.addItem(new BookingItem(i.type(), i.description(),
                i.flightNumber(), i.departureDate(), i.quantity(), i.unitPrice())));
        return BookingResponse.from(bookings.save(booking));
    }
}
