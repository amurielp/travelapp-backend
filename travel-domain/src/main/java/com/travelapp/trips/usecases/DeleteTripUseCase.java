package com.travelapp.trips.usecases;

import com.travelapp.trips.ports.TripRepository;
import com.travelapp.shared.exceptions.TripNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class DeleteTripUseCase {
    private final TripRepository trips;

    @Transactional
    public void execute(UUID tripId) {
        if (!trips.findById(tripId).isPresent()) {
            throw new TripNotFoundException(tripId);
        }
        trips.deleteById(tripId);
    }
}
