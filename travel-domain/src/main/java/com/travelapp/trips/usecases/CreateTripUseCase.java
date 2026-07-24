package com.travelapp.trips.usecases;

import com.travelapp.trips.domain.*;
import com.travelapp.trips.ports.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateTripUseCase {

    private final TripRepository tripRepository;

    @Transactional
    public Trip execute(CreateTripCommand cmd) {
        var trip = Trip.builder()
            .id(UUID.randomUUID())
            .ownerId(cmd.ownerId())
            .title(cmd.title())
            .description(cmd.description())
            .startDate(cmd.startDate())
            .endDate(cmd.endDate())
            .baseCurrency(cmd.baseCurrency())
            .status(TripStatus.PLANNING)
            .isPublic(false)
            .build();

        return tripRepository.save(trip);
    }
}
