package com.autopilot.airline.booking;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/** A line item on a booking: a flight segment or an extra such as baggage or a seat. */
@Entity
public class BookingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private BookingItemType type;

    private String description;
    private String flightNumber;
    private LocalDate departureDate;
    private int quantity;
    private BigDecimal unitPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    protected BookingItem() {
    }

    public BookingItem(BookingItemType type, String description, String flightNumber,
            LocalDate departureDate, int quantity, BigDecimal unitPrice) {
        this.type = type;
        this.description = description;
        this.flightNumber = flightNumber;
        this.departureDate = departureDate;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Long getId() {
        return id;
    }

    public BookingItemType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
}
