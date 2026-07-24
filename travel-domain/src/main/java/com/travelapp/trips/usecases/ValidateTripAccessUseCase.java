package com.travelapp.trips.usecases;
import com.travelapp.shared.exceptions.*;
import com.travelapp.trips.ports.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class ValidateTripAccessUseCase {
    private final TripRepository trips;

    public void execute(UUID tripId, UUID userId) {
        var trip = trips.findById(tripId).orElseThrow(() -> new TripNotFoundException(tripId));
        if (!trip.getOwnerId().equals(userId)) {
            throw new AccessDeniedException("User " + userId + " has no access to trip " + tripId);
        }
    }
}
