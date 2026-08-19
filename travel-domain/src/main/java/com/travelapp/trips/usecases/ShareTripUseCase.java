package com.travelapp.trips.usecases;

import com.travelapp.shared.exceptions.TripNotFoundException;
import com.travelapp.trips.domain.Trip;
import com.travelapp.trips.ports.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShareTripUseCase {

    private final TripRepository tripRepository;

    @Transactional(readOnly = true)
    public TripShareResult execute(UUID tripId) {
        Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new TripNotFoundException(tripId));
        String deepLink = "https://travelapp.com/trips/" + tripId;
        String webUrl = trip.getPublicSlug() != null
            ? "https://travelapp.com/trips/public/" + trip.getPublicSlug()
            : deepLink;
        return new TripShareResult(deepLink, webUrl, null);
    }
}
