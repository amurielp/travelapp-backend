package com.travelapp.trips.ports;

import com.travelapp.trips.domain.Trip;
import java.util.*;

public interface TripRepository {
    Trip save(Trip trip);
    Optional<Trip> findById(UUID id);
    List<Trip> findByOwnerId(UUID ownerId);
    List<Trip> findSharedWith(UUID userId);
    void deleteById(UUID id);
    boolean existsByPublicSlug(String slug);
    List<Trip> findAllActive();
}
