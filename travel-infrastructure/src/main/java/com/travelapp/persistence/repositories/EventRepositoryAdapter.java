package com.travelapp.persistence.repositories;

import com.travelapp.events.domain.TravelEvent;
import com.travelapp.events.ports.EventRepository;
import com.travelapp.persistence.entities.*;
import com.travelapp.persistence.mappers.DetailEntityMapper;
import com.travelapp.persistence.mappers.EventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class EventRepositoryAdapter implements EventRepository {

    private final EventJpaRepository         eventJpa;
    private final FlightJpaRepository        flightJpa;
    private final AccommodationJpaRepository accommodationJpa;
    private final ActivityJpaRepository      activityJpa;
    private final TransportJpaRepository     transportJpa;
    private final EsimJpaRepository          esimJpa;
    private final InsuranceJpaRepository     insuranceJpa;
    private final EventMapper                mapper;
    private final DetailEntityMapper         detailMapper;

    @Override
    @Transactional
    public TravelEvent save(TravelEvent event) {
        EventEntity entity = mapper.toEntity(event);
        EventEntity saved  = eventJpa.save(entity);
        syncDetails(saved, event);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<TravelEvent> findById(UUID id) {
        return eventJpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<TravelEvent> findByTripId(UUID tripId) {
        return eventJpa.findByTripIdOrderByStartDatetimeAsc(tripId)
                       .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<TravelEvent> findByTripIdAndDateRange(UUID tripId, LocalDate from, LocalDate to) {
        OffsetDateTime start = from.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime();
        OffsetDateTime end   = to.plusDays(1).atStartOfDay(ZoneOffset.UTC).toOffsetDateTime();
        return eventJpa.findByTripIdAndDateRange(tripId, start, end)
                       .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<TravelEvent> findFreeSlotsByTripIdAndDate(UUID tripId, LocalDate date) {
        OffsetDateTime from = date.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime();
        OffsetDateTime to   = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toOffsetDateTime();
        return eventJpa.findByTripIdAndDateRange(tripId, from, to)
                       .stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) { eventJpa.deleteById(id); }

    // ── Detail sync ───────────────────────────────────────────────────────────

    private void syncDetails(EventEntity saved, TravelEvent event) {
        syncFlight(saved, event);
        syncAccommodation(saved, event);
        syncActivity(saved, event);
        syncTransport(saved, event);
        syncEsim(saved, event);
        syncInsurance(saved, event);
    }

    private void syncFlight(EventEntity saved, TravelEvent event) {
        if (event.getFlight() != null) {
            FlightEntity fe = flightJpa.findByEventId(saved.getId())
                .orElse(new FlightEntity());
            detailMapper.updateFlight(event.getFlight(), fe);
            fe.setEvent(saved);
            saved.setFlight(flightJpa.save(fe));
        } else {
            flightJpa.findByEventId(saved.getId()).ifPresent(flightJpa::delete);
            saved.setFlight(null);
        }
    }

    private void syncAccommodation(EventEntity saved, TravelEvent event) {
        if (event.getAccommodation() != null) {
            AccommodationEntity ae = accommodationJpa.findByEventId(saved.getId())
                .orElse(new AccommodationEntity());
            detailMapper.updateAccommodation(event.getAccommodation(), ae);
            ae.setEvent(saved);
            saved.setAccommodation(accommodationJpa.save(ae));
        } else {
            accommodationJpa.findByEventId(saved.getId()).ifPresent(accommodationJpa::delete);
            saved.setAccommodation(null);
        }
    }

    private void syncActivity(EventEntity saved, TravelEvent event) {
        if (event.getActivity() != null) {
            ActivityEntity ae = activityJpa.findByEventId(saved.getId())
                .orElse(new ActivityEntity());
            detailMapper.updateActivity(event.getActivity(), ae);
            ae.setEvent(saved);
            saved.setActivity(activityJpa.save(ae));
        } else {
            activityJpa.findByEventId(saved.getId()).ifPresent(activityJpa::delete);
            saved.setActivity(null);
        }
    }

    private void syncTransport(EventEntity saved, TravelEvent event) {
        if (event.getTransport() != null) {
            TransportEntity te = transportJpa.findByEventId(saved.getId())
                .orElse(new TransportEntity());
            detailMapper.updateTransport(event.getTransport(), te);
            te.setEvent(saved);
            saved.setTransport(transportJpa.save(te));
        } else {
            transportJpa.findByEventId(saved.getId()).ifPresent(transportJpa::delete);
            saved.setTransport(null);
        }
    }

    private void syncEsim(EventEntity saved, TravelEvent event) {
        if (event.getEsim() != null) {
            EsimEntity ee = esimJpa.findByEventId(saved.getId())
                .orElse(new EsimEntity());
            detailMapper.updateEsim(event.getEsim(), ee);
            ee.setEvent(saved);
            saved.setEsim(esimJpa.save(ee));
        } else {
            esimJpa.findByEventId(saved.getId()).ifPresent(esimJpa::delete);
            saved.setEsim(null);
        }
    }

    private void syncInsurance(EventEntity saved, TravelEvent event) {
        if (event.getInsurance() != null) {
            InsuranceEntity ie = insuranceJpa.findByEventId(saved.getId())
                .orElse(new InsuranceEntity());
            detailMapper.updateInsurance(event.getInsurance(), ie);
            ie.setEvent(saved);
            saved.setInsurance(insuranceJpa.save(ie));
        } else {
            insuranceJpa.findByEventId(saved.getId()).ifPresent(insuranceJpa::delete);
            saved.setInsurance(null);
        }
    }
}
