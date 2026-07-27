package com.travelapp.gaps.usecases;

import com.travelapp.events.domain.*;
import com.travelapp.events.ports.EventRepository;
import com.travelapp.gaps.domain.*;
import com.travelapp.gaps.ports.TripGapRepository;
import com.travelapp.trips.domain.Trip;
import com.travelapp.trips.ports.TripRepository;
import com.travelapp.shared.exceptions.TripNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DetectTripGapsUseCase {

    private static final int MIN_CONNECTION_MINUTES   = 60;
    private static final int CANCELLATION_ALERT_HOURS = 48;
    private static final int PAYMENT_DEADLINE_HOURS   = 72;

    private final TripRepository    trips;
    private final EventRepository   events;
    private final TripGapRepository gaps;

    @Transactional
    public List<TripGap> execute(UUID tripId) {
        var trip = trips.findById(tripId)
            .orElseThrow(() -> new TripNotFoundException(tripId));

        var allEvents = events.findByTripId(tripId).stream()
            .filter(e -> e.getStatus() != EventStatus.CANCELLED)
            .toList();

        var detected = new ArrayList<TripGap>();

        detected.addAll(detectTransportGaps(trip, allEvents));
        detected.addAll(detectAccommodationGaps(trip, allEvents));
        detected.addAll(detectTightConnections(allEvents));
        detected.addAll(detectCancellationDeadlines(allEvents));
        detected.addAll(detectPendingPaymentDeadlines(allEvents));

        gaps.deleteByTripId(tripId);
        if (!detected.isEmpty()) {
            gaps.saveAll(detected);
        }

        log.info("detectGaps tripId={} found={}", tripId, detected.size());
        return detected;
    }

    // ── 1. Huecos de transporte entre destinos ────────────────

    private List<TripGap> detectTransportGaps(Trip trip, List<TravelEvent> allEvents) {
        var gaps = new ArrayList<TripGap>();

        var anchors = allEvents.stream()
            .filter(this::isCityAnchor)
            .filter(e -> anchorStartDate(e) != null)
            .sorted(Comparator.comparing(this::anchorStartDate))
            .toList();

        if (anchors.size() < 2) return gaps;

        for (int i = 0; i < anchors.size() - 1; i++) {
            var current = anchors.get(i);
            var next    = anchors.get(i + 1);

            var currentCheckout = anchorEndDate(current);
            var nextCheckin     = anchorStartDate(next);

            if (sameCity(current, next)) continue;

            var hasTransport = hasTransportBetween(allEvents,
                currentCheckout, nextCheckin,
                current.getLocationName(), next.getLocationName());

            if (!hasTransport) {
                var onlyDrafts = hasOnlyDraftTransportBetween(allEvents, currentCheckout, nextCheckin);

                gaps.add(TripGap.builder()
                    .id(UUID.randomUUID())
                    .tripId(trip.getId())
                    .gapType(onlyDrafts ? GapType.TRANSPORT_ONLY_DRAFT : GapType.NO_TRANSPORT_BETWEEN_DESTINATIONS)
                    .severity(onlyDrafts ? GapSeverity.WARNING : GapSeverity.ERROR)
                    .affectedFrom(currentCheckout)
                    .affectedTo(nextCheckin)
                    .originCity(current.getLocationName())
                    .destinationCity(next.getLocationName())
                    .eventIdFrom(current.getId())
                    .eventIdTo(next.getId())
                    .suggestionText(buildTransportSuggestion(current, next))
                    .status(GapStatus.OPEN)
                    .detectedAt(OffsetDateTime.now())
                    .build());
            }
        }
        return gaps;
    }

    // ── 2. Días sin alojamiento ───────────────────────────────

    private List<TripGap> detectAccommodationGaps(Trip trip, List<TravelEvent> allEvents) {
        var gaps = new ArrayList<TripGap>();

        var coveredDays = allEvents.stream()
            .filter(this::isCityAnchor)
            .flatMap(e -> {
                var from = anchorStartDate(e);
                var to   = anchorEndDate(e);
                if (from == null || to == null) return Stream.empty();
                return from.datesUntil(to.plusDays(1));
            })
            .collect(Collectors.toSet());

        var tripDays = trip.getStartDate().datesUntil(trip.getEndDate().plusDays(1)).toList();

        LocalDate gapStart = null;
        for (var day : tripDays) {
            if (!coveredDays.contains(day)) {
                if (gapStart == null) gapStart = day;
            } else {
                if (gapStart != null) {
                    var finalGapStart = gapStart;
                    gaps.add(TripGap.builder()
                        .id(UUID.randomUUID()).tripId(trip.getId())
                        .gapType(GapType.DAYS_WITHOUT_ACCOMMODATION)
                        .severity(GapSeverity.ERROR)
                        .affectedFrom(finalGapStart).affectedTo(day.minusDays(1))
                        .suggestionText("Sin alojamiento del " + finalGapStart + " al " + day.minusDays(1))
                        .status(GapStatus.OPEN).detectedAt(OffsetDateTime.now())
                        .build());
                    gapStart = null;
                }
            }
        }
        if (gapStart != null) {
            var finalGapStart = gapStart;
            gaps.add(TripGap.builder()
                .id(UUID.randomUUID()).tripId(trip.getId())
                .gapType(GapType.DAYS_WITHOUT_ACCOMMODATION)
                .severity(GapSeverity.ERROR)
                .affectedFrom(finalGapStart).affectedTo(trip.getEndDate())
                .suggestionText("Sin alojamiento del " + finalGapStart + " al " + trip.getEndDate())
                .status(GapStatus.OPEN).detectedAt(OffsetDateTime.now())
                .build());
        }
        return gaps;
    }

    // ── 3. Conexiones ajustadas ───────────────────────────────

    private List<TripGap> detectTightConnections(List<TravelEvent> allEvents) {
        var gaps = new ArrayList<TripGap>();

        // departure = event.startDatetime, arrival = event.endDatetime
        var flights = allEvents.stream()
            .filter(e -> e.getType() == EventType.FLIGHT && e.getFlight() != null)
            .filter(e -> e.getStartDatetime() != null)
            .sorted(Comparator.comparing(TravelEvent::getStartDatetime))
            .toList();

        for (int i = 0; i < flights.size() - 1; i++) {
            var current = flights.get(i);
            var next    = flights.get(i + 1);

            if (current.getEndDatetime() == null || next.getStartDatetime() == null) continue;

            if (!Objects.equals(current.getFlight().getDestinationIata(),
                                 next.getFlight().getOriginIata())) continue;

            var diff = Duration.between(current.getEndDatetime(), next.getStartDatetime());
            if (diff.toMinutes() < MIN_CONNECTION_MINUTES && diff.toMinutes() > 0) {
                gaps.add(TripGap.builder()
                    .id(UUID.randomUUID()).tripId(current.getTripId())
                    .gapType(GapType.TIGHT_CONNECTION).severity(GapSeverity.ERROR)
                    .affectedFrom(current.getEndDatetime().toLocalDate())
                    .originIata(current.getFlight().getDestinationIata())
                    .eventIdFrom(current.getId()).eventIdTo(next.getId())
                    .suggestionText("Solo " + diff.toMinutes() + " min de conexión en " +
                        current.getFlight().getDestinationIata() + " — mínimo recomendado: 60 min")
                    .status(GapStatus.OPEN).detectedAt(OffsetDateTime.now())
                    .build());
            }
        }
        return gaps;
    }

    // ── 4. Deadlines de cancelación ───────────────────────────

    private List<TripGap> detectCancellationDeadlines(List<TravelEvent> allEvents) {
        var gaps = new ArrayList<TripGap>();
        var threshold = OffsetDateTime.now().plusHours(CANCELLATION_ALERT_HOURS);

        allEvents.stream()
            .filter(e -> e.getType() == EventType.FLIGHT && e.getFlight() != null)
            .filter(e -> {
                var deadline = e.getFlight().getCancellationDeadline();
                return deadline != null && deadline.isBefore(threshold) && deadline.isAfter(OffsetDateTime.now());
            })
            .forEach(e -> gaps.add(TripGap.builder()
                .id(UUID.randomUUID()).tripId(e.getTripId())
                .gapType(GapType.CANCELLATION_DEADLINE_NEAR).severity(GapSeverity.WARNING)
                .affectedFrom(e.getFlight().getCancellationDeadline().toLocalDate())
                .eventIdFrom(e.getId())
                .suggestionText("Cancelación gratuita de " + e.getTitle() + " vence el " +
                    e.getFlight().getCancellationDeadline())
                .status(GapStatus.OPEN).detectedAt(OffsetDateTime.now())
                .build()));

        allEvents.stream()
            .filter(e -> e.getType() == EventType.ACCOMMODATION && e.getAccommodation() != null)
            .filter(e -> {
                var deadline = e.getAccommodation().getFreeCancellationUntil();
                return deadline != null && deadline.isBefore(threshold) && deadline.isAfter(OffsetDateTime.now());
            })
            .forEach(e -> gaps.add(TripGap.builder()
                .id(UUID.randomUUID()).tripId(e.getTripId())
                .gapType(GapType.CANCELLATION_DEADLINE_NEAR).severity(GapSeverity.WARNING)
                .affectedFrom(e.getAccommodation().getFreeCancellationUntil().toLocalDate())
                .eventIdFrom(e.getId())
                .suggestionText("Cancelación gratuita de " + e.getAccommodation().getName() + " vence el " +
                    e.getAccommodation().getFreeCancellationUntil())
                .status(GapStatus.OPEN).detectedAt(OffsetDateTime.now())
                .build()));

        return gaps;
    }

    // ── 5. Pagos pendientes con deadline próximo ──────────────

    private List<TripGap> detectPendingPaymentDeadlines(List<TravelEvent> allEvents) {
        var gaps      = new ArrayList<TripGap>();
        var threshold = OffsetDateTime.now().plusHours(PAYMENT_DEADLINE_HOURS);

        allEvents.stream()
            .filter(e -> e.getType() == EventType.FLIGHT && e.getFlight() != null)
            .filter(e -> {
                var f = e.getFlight();
                return f.getPurchaseStatus() == com.travelapp.shared.domain.PurchaseStatus.PENDING
                    && f.getCancellationDeadline() != null
                    && f.getCancellationDeadline().isBefore(threshold)
                    && f.getPurchasedAt() == null;
            })
            .forEach(e -> gaps.add(TripGap.builder()
                .id(UUID.randomUUID()).tripId(e.getTripId())
                .gapType(GapType.PENDING_PAYMENT_DEADLINE).severity(GapSeverity.WARNING)
                .affectedFrom(e.getFlight().getCancellationDeadline().toLocalDate())
                .eventIdFrom(e.getId())
                .suggestionText("El vuelo " + e.getTitle() + " está PENDIENTE de pago. " +
                    "El precio bloqueado vence el " + e.getFlight().getCancellationDeadline())
                .status(GapStatus.OPEN).detectedAt(OffsetDateTime.now())
                .build()));

        return gaps;
    }

    // ── Helpers ───────────────────────────────────────────────

    private boolean isCityAnchor(TravelEvent e) {
        if (e.getType() == EventType.ACCOMMODATION) return e.getAccommodation() != null;
        if (e.getType() == EventType.DESTINATION)
            return e.getStartDatetime() != null && e.getEndDatetime() != null;
        return false;
    }

    private LocalDate anchorStartDate(TravelEvent e) {
        if (e.getType() == EventType.ACCOMMODATION)
            return e.getStartDatetime() != null ? e.getStartDatetime().toLocalDate() : null;
        if (e.getType() == EventType.DESTINATION)
            return e.getStartDatetime() != null ? e.getStartDatetime().toLocalDate() : null;
        return null;
    }

    private LocalDate anchorEndDate(TravelEvent e) {
        if (e.getType() == EventType.ACCOMMODATION)
            return e.getEndDatetime() != null ? e.getEndDatetime().toLocalDate() : null;
        if (e.getType() == EventType.DESTINATION)
            return e.getEndDatetime() != null ? e.getEndDatetime().toLocalDate() : null;
        return null;
    }

    private boolean sameCity(TravelEvent a, TravelEvent b) {
        var cityA = a.getLocationName();
        var cityB = b.getLocationName();
        if (cityA == null || cityB == null) return false;
        return cityA.equalsIgnoreCase(cityB);
    }

    private boolean hasTransportBetween(List<TravelEvent> events,
            LocalDate from, LocalDate to, String origin, String destination) {
        return events.stream()
            .filter(e -> e.getType() == EventType.FLIGHT || e.getType() == EventType.TRANSPORT)
            .filter(e -> e.getFlight() != null || e.getTransport() != null)
            .anyMatch(e -> {
                var start = e.getStartDatetime() != null ? e.getStartDatetime().toLocalDate() : null;
                if (start == null) return false;
                if (start.isBefore(from) || start.isAfter(to)) return false;
                if (e.getType() == EventType.FLIGHT && e.getFlight() != null) {
                    var f = e.getFlight();
                    return f.getPurchaseStatus() != null
                        && f.getPurchaseStatus().isActive()
                        && f.getPurchaseStatus() != com.travelapp.shared.domain.PurchaseStatus.DRAFT;
                }
                return true;
            });
    }

    private boolean hasOnlyDraftTransportBetween(List<TravelEvent> events,
            LocalDate from, LocalDate to) {
        return events.stream()
            .filter(e -> e.getType() == EventType.FLIGHT || e.getType() == EventType.TRANSPORT)
            .anyMatch(e -> {
                var start = e.getStartDatetime() != null ? e.getStartDatetime().toLocalDate() : null;
                if (start == null) return false;
                if (start.isBefore(from) || start.isAfter(to)) return false;
                if (e.getType() == EventType.FLIGHT && e.getFlight() != null) {
                    return e.getFlight().getPurchaseStatus() ==
                        com.travelapp.shared.domain.PurchaseStatus.DRAFT;
                }
                return false;
            });
    }

    private String buildTransportSuggestion(TravelEvent from, TravelEvent to) {
        var origin = from.getLocationName() != null ? from.getLocationName() : "origen";
        var dest   = to.getLocationName()   != null ? to.getLocationName()   : "destino";
        return "Falta transporte de " + origin + " a " + dest +
            ". Opciones: vuelo, tren, autobús, coche de alquiler o taxi.";
    }
}
