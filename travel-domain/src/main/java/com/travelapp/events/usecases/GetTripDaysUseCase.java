package com.travelapp.events.usecases;

import com.travelapp.events.domain.*;
import com.travelapp.events.ports.EventRepository;
import com.travelapp.trips.ports.TripRepository;
import com.travelapp.shared.exceptions.TripNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;
import java.util.stream.*;

@Service
@RequiredArgsConstructor
public class GetTripDaysUseCase {

    private final TripRepository  tripRepository;
    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    public List<TripDaySummary> execute(UUID tripId) {
        var trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new TripNotFoundException(tripId));

        // Todos los días del viaje
        var days = trip.getStartDate().datesUntil(trip.getEndDate().plusDays(1))
            .collect(Collectors.toList());

        // Todos los eventos del viaje
        var allEvents = eventRepository.findByTripId(tripId);

        return days.stream()
            .map(date -> buildDaySummary(date, allEvents))
            .toList();
    }

    private TripDaySummary buildDaySummary(LocalDate date, List<TravelEvent> allEvents) {
        // Eventos que tocan este día
        var dayEvents = allEvents.stream()
            .filter(e -> eventTouchesDay(e, date))
            .toList();

        // Alojamiento del día → ciudad del destino
        var accommodation = dayEvents.stream()
            .filter(e -> e.getType() == EventType.ACCOMMODATION)
            .findFirst();

        // Calcular huecos libres (9:00 - 22:00 como ventana de actividad)
        var freeSlots = calculateFreeSlots(date, dayEvents);

        var occupancy = computeOccupancy(freeSlots);

        return TripDaySummary.builder()
            .date(date)
            .destinationCity(accommodation
                .map(e -> e.getAccommodation() != null ? e.getAccommodation().getCity() : e.getLocationName())
                .orElse(null))
            .latitude(accommodation.map(TravelEvent::getLatitude).orElse(null))
            .longitude(accommodation.map(TravelEvent::getLongitude).orElse(null))
            .occupancy(occupancy)
            .freeSlots(freeSlots)
            .eventCount(dayEvents.size())
            .build();
    }

    private boolean eventTouchesDay(TravelEvent event, LocalDate date) {
        var start = event.getStartDatetime();
        if (start == null) return false;
        var startDate = start.toLocalDate();
        var endDate   = event.getEndDatetime() != null
                        ? event.getEndDatetime().toLocalDate()
                        : startDate;
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Calcula los huecos libres del día entre 09:00 y 22:00.
     * Algoritmo: línea de tiempo con eventos ordenados, resta los bloques ocupados.
     */
    private List<FreeSlot> calculateFreeSlots(LocalDate date, List<TravelEvent> events) {
        LocalTime WINDOW_START = LocalTime.of(9, 0);
        LocalTime WINDOW_END   = LocalTime.of(22, 0);

        // Solo eventos con hora concreta (no all_day, no alojamiento)
        var timedEvents = events.stream()
            .filter(e -> !e.isAllDay() && e.getType() != EventType.ACCOMMODATION)
            .filter(e -> e.getStartDatetime() != null && e.getEndDatetime() != null)
            .sorted(Comparator.comparing(e -> e.getStartDatetime().toLocalTime()))
            .toList();

        if (timedEvents.isEmpty()) {
            return List.of(new FreeSlot(WINDOW_START, WINDOW_END));
        }

        var slots  = new ArrayList<FreeSlot>();
        var cursor = WINDOW_START;

        for (var event : timedEvents) {
            var eStart = event.getStartDatetime().toLocalTime();
            var eEnd   = event.getEndDatetime().toLocalTime();

            // Clip al día actual para eventos multi-día
            if (eStart.isBefore(WINDOW_START)) eStart = WINDOW_START;
            if (eEnd.isAfter(WINDOW_END))       eEnd   = WINDOW_END;

            if (cursor.isBefore(eStart) && Duration.between(cursor, eStart).toMinutes() >= 30) {
                slots.add(new FreeSlot(cursor, eStart));
            }
            if (eEnd.isAfter(cursor)) cursor = eEnd;
        }

        if (cursor.isBefore(WINDOW_END) && Duration.between(cursor, WINDOW_END).toMinutes() >= 30) {
            slots.add(new FreeSlot(cursor, WINDOW_END));
        }

        return slots;
    }

    private String computeOccupancy(List<FreeSlot> freeSlots) {
        int totalFreeMinutes = freeSlots.stream()
            .mapToInt(s -> (int) Duration.between(s.from(), s.to()).toMinutes())
            .sum();

        if (totalFreeMinutes > 360) return "free";       // más de 6h libres
        if (totalFreeMinutes > 0)   return "partial";
        return "full";
    }
}
