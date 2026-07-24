package com.travelapp.events.usecases;

import com.travelapp.events.domain.*;
import com.travelapp.events.ports.EventRepository;
import com.travelapp.shared.exceptions.EventOverlapException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateEventUseCase {

    private final EventRepository eventRepository;

    @Transactional
    public TravelEvent execute(CreateEventCommand cmd) {

        // Validar solapamiento para vuelos y actividades con hora concreta
        if (cmd.endDatetime() != null && cmd.type() != EventType.ACCOMMODATION) {
            var overlapping = eventRepository
                .findByTripIdAndDateRange(cmd.tripId(), cmd.startDatetime().toLocalDate(),
                                          cmd.endDatetime().toLocalDate())
                .stream()
                .filter(e -> e.getType() != EventType.ACCOMMODATION)
                .filter(e -> e.overlapsWith(buildDraftEvent(cmd)))
                .toList();

            if (!overlapping.isEmpty()) {
                throw new EventOverlapException(overlapping.get(0));
            }
        }

        var event = TravelEvent.builder()
            .id(UUID.randomUUID())
            .tripId(cmd.tripId())
            .documentId(cmd.documentId())
            .type(cmd.type())
            .title(cmd.title())
            .notes(cmd.notes())
            .color(cmd.color())
            .startDatetime(cmd.startDatetime())
            .endDatetime(cmd.endDatetime())
            .allDay(cmd.allDay())
            .timezone(cmd.timezone())
            .status(EventStatus.CONFIRMED)
            .source(cmd.source() != null ? cmd.source() : EventSource.MANUAL)
            .locationName(cmd.locationName())
            .latitude(cmd.latitude())
            .longitude(cmd.longitude())
            .flight(cmd.flight())
            .accommodation(cmd.accommodation())
            .activity(cmd.activity())
            .transport(cmd.transport())
            .build();

        return eventRepository.save(event);
    }

    private TravelEvent buildDraftEvent(CreateEventCommand cmd) {
        return TravelEvent.builder()
            .startDatetime(cmd.startDatetime())
            .endDatetime(cmd.endDatetime())
            .build();
    }
}
