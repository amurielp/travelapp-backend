package com.travelapp.trips.usecases;

import com.travelapp.trips.domain.*;
import com.travelapp.trips.ports.TripRepository;
import com.travelapp.shared.exceptions.TripNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class PublishTripUseCase {
    private final TripRepository trips;

    @Transactional
    public Trip execute(UUID tripId) {
        var trip = trips.findById(tripId)
            .orElseThrow(() -> new TripNotFoundException(tripId));

        if (trip.getStatus() != TripStatus.PLANNING && trip.getStatus() != TripStatus.ACTIVE) {
            throw new IllegalStateException("Only PLANNING or ACTIVE trips can be published");
        }

        trip.setPublic(true);
        if (trip.getPublicSlug() == null) {
            trip.setPublicSlug(generateSlug(trip.getTitle()));
        }

        return trips.save(trip);
    }

    private String generateSlug(String title) {
        return title.toLowerCase()
            .replaceAll("[^a-z0-9\\s-]", "")
            .replaceAll("\\s+", "-")
            .replaceAll("-+", "-")
            + "-" + UUID.randomUUID().toString().substring(0, 6);
    }
}
