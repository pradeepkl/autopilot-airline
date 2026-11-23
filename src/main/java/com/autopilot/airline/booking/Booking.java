package com.autopilot.airline.booking;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String passengerName;
    private LocalDate bookingDate;
    private String status;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingItem> items = new ArrayList<>();

    protected Booking() {
    }

    public Booking(String passengerName, LocalDate bookingDate, String status) {
        this.passengerName = passengerName;
        this.bookingDate = bookingDate;
        this.status = status;
    }

    public void addItem(BookingItem item) {
        item.setBooking(this);
        items.add(item);
    }

    public Long getId() {
        return id;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public String getStatus() {
        return status;
    }

    public List<BookingItem> getItems() {
        return items;
    }
}
