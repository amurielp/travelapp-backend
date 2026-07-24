package com.travelapp.events.usecases;

import com.travelapp.events.domain.*;
import com.travelapp.events.ports.EventRepository;
import com.travelapp.shared.exceptions.EventNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateEventUseCase {

    private final EventRepository eventRepository;

    @Transactional
    public TravelEvent execute(UpdateEventCommand cmd) {
        var event = eventRepository.findById(cmd.eventId())
            .filter(e -> e.getTripId().equals(cmd.tripId()))
            .orElseThrow(() -> new EventNotFoundException(cmd.eventId()));

        event.update(cmd);
        return eventRepository.save(event);
    }
}
