package com.autopilot.airline.booking;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Load items together with the booking so no lazy loading is needed later.
    @Override
    @EntityGraph(attributePaths = "items")
    List<Booking> findAll();

    @Override
    @EntityGraph(attributePaths = "items")
    Optional<Booking> findById(Long id);
}
