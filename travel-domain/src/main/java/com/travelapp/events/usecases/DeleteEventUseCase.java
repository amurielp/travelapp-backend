package com.travelapp.events.usecases;

import com.travelapp.events.ports.EventRepository;
import com.travelapp.shared.exceptions.EventNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteEventUseCase {

    private final EventRepository eventRepository;

    @Transactional
    public void execute(UUID eventId, UUID tripId) {
        eventRepository.findById(eventId)
            .filter(e -> e.getTripId().equals(tripId))
            .orElseThrow(() -> new EventNotFoundException(eventId));

        eventRepository.deleteById(eventId);
    }
}
