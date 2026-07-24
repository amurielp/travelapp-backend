package com.travelapp.events.usecases;

import com.travelapp.events.domain.*;
import com.travelapp.events.ports.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GetEventsUseCase {

    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    public List<TravelEvent> execute(GetEventsQuery query) {
        Stream<TravelEvent> events = (query.from() != null && query.to() != null)
            ? eventRepository.findByTripIdAndDateRange(query.tripId(), query.from(), query.to()).stream()
            : eventRepository.findByTripId(query.tripId()).stream();

        if (query.type() != null) {
            var type = EventType.valueOf(query.type());
            events = events.filter(e -> e.getType() == type);
        }
        if (query.status() != null) {
            var status = EventStatus.valueOf(query.status());
            events = events.filter(e -> e.getStatus() == status);
        }

        return events.toList();
    }
}
