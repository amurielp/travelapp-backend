package com.travelapp.trips.usecases;

import com.travelapp.trips.domain.Trip;
import com.travelapp.trips.ports.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class GetUserTripsUseCase {
    private final TripRepository trips;

    @Transactional(readOnly = true)
    public List<Trip> execute(UUID userId) {
        return trips.findByOwnerId(userId);
    }
}
