package com.travelapp.persistence.repositories;

import com.travelapp.events.domain.TravelEvent;
import com.travelapp.events.ports.EventRepository;
import com.travelapp.persistence.mappers.EventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.time.*;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class EventRepositoryAdapter implements EventRepository {

    private final EventJpaRepository jpa;
    private final EventMapper        mapper;

    @Override
    public TravelEvent save(TravelEvent event) {
        return mapper.toDomain(jpa.save(mapper.toEntity(event)));
    }

    @Override
    public Optional<TravelEvent> findById(UUID id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<TravelEvent> findByTripId(UUID tripId) {
        return jpa.findByTripIdOrderByStartDatetimeAsc(tripId)
                  .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<TravelEvent> findByTripIdAndDateRange(UUID tripId, LocalDate from, LocalDate to) {
        OffsetDateTime start = from.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime();
        OffsetDateTime end   = to.plusDays(1).atStartOfDay(ZoneOffset.UTC).toOffsetDateTime();
        return jpa.findByTripIdAndDateRange(tripId, start, end)
                  .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<TravelEvent> findFreeSlotsByTripIdAndDate(UUID tripId, LocalDate date) {
        // Returns all events on the given date; the use case computes free slots from them
        OffsetDateTime from = date.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime();
        OffsetDateTime to   = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toOffsetDateTime();
        return jpa.findByTripIdAndDateRange(tripId, from, to)
                  .stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) { jpa.deleteById(id); }
}
