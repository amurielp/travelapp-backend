package com.travelapp.events.ports;

import com.travelapp.events.domain.*;
import java.time.*;
import java.util.*;

public interface EventRepository {
    TravelEvent save(TravelEvent event);
    Optional<TravelEvent> findById(UUID id);
    List<TravelEvent> findByTripId(UUID tripId);
    List<TravelEvent> findByTripIdAndDateRange(UUID tripId, LocalDate from, LocalDate to);
    List<TravelEvent> findFreeSlotsByTripIdAndDate(UUID tripId, LocalDate date);
    void deleteById(UUID id);
}
