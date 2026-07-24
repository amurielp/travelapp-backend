package com.travelapp.trips.usecases;

import com.travelapp.trips.domain.Trip;
import com.travelapp.trips.ports.TripRepository;
import com.travelapp.shared.exceptions.TripNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class GetTripUseCase {
    private final TripRepository trips;

    @Transactional(readOnly = true)
    public Trip execute(UUID tripId) {
        return trips.findById(tripId)
            .orElseThrow(() -> new TripNotFoundException(tripId));
    }
}
