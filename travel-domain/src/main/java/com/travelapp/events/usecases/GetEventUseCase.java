package com.travelapp.events.usecases;

import com.travelapp.events.domain.TravelEvent;
import com.travelapp.events.ports.EventRepository;
import com.travelapp.shared.exceptions.EventNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetEventUseCase {

    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    public TravelEvent execute(UUID eventId, UUID tripId) {
        return eventRepository.findById(eventId)
            .filter(e -> e.getTripId().equals(tripId))  // asegurar que pertenece al trip
            .orElseThrow(() -> new EventNotFoundException(eventId));
    }
}
