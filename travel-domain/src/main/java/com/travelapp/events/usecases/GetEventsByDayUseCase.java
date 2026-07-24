package com.travelapp.events.usecases;

import com.travelapp.events.domain.TravelEvent;
import com.travelapp.events.ports.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetEventsByDayUseCase {

    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    public List<TravelEvent> execute(UUID tripId, LocalDate date) {
        return eventRepository
            .findByTripIdAndDateRange(tripId, date, date)
            .stream()
            .sorted(java.util.Comparator.comparing(e ->
                e.getStartDatetime() != null ? e.getStartDatetime() :
                java.time.OffsetDateTime.MIN))
            .toList();
    }
}
