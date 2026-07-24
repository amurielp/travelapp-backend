package com.travelapp.trips.usecases;

import com.travelapp.trips.domain.Trip;
import com.travelapp.trips.ports.TripRepository;
import com.travelapp.shared.exceptions.TripNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class UpdateTripUseCase {
    private final TripRepository trips;

    @Transactional
    public Trip execute(UpdateTripCommand cmd) {
        var trip = trips.findById(cmd.tripId())
            .orElseThrow(() -> new TripNotFoundException(cmd.tripId()));

        if (cmd.title()        != null) trip.setTitle(cmd.title());
        if (cmd.description()  != null) trip.setDescription(cmd.description());
        if (cmd.startDate()    != null) trip.setStartDate(cmd.startDate());
        if (cmd.endDate()      != null) trip.setEndDate(cmd.endDate());
        if (cmd.baseCurrency() != null) trip.setBaseCurrency(cmd.baseCurrency());
        if (cmd.coverImageUrl()!= null) trip.setCoverImageUrl(cmd.coverImageUrl());

        return trips.save(trip);
    }
}
