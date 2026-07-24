package com.travelapp.notifications.usecases;

import com.travelapp.events.domain.*;
import com.travelapp.events.ports.EventRepository;
import com.travelapp.notifications.domain.NotificationType;
import com.travelapp.notifications.ports.NotificationSender;
import com.travelapp.shared.domain.PurchaseStatus;
import com.travelapp.trips.ports.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;

@Slf4j @Service @RequiredArgsConstructor
public class SendCancellationDeadlineAlertUseCase {

    private final TripRepository     trips;
    private final EventRepository    events;
    private final NotificationSender sender;

    /** Llamado por el scheduler de Quartz cada mañana a las 08:00 */
    @Transactional(readOnly = true)
    public void execute() {
        var threshold = OffsetDateTime.now().plusHours(48);

        // Buscar todos los viajes activos
        trips.findAllActive().forEach(trip -> {
            var tripEvents = events.findByTripId(trip.getId());

            tripEvents.stream()
                .filter(e -> e.getType() == EventType.FLIGHT && e.getFlight() != null)
                .filter(e -> {
                    var f = e.getFlight();
                    if (f.getPurchaseStatus() == null || !f.getPurchaseStatus().isCancellable()) return false;
                    var deadline = f.getCancellationDeadline();
                    return deadline != null && deadline.isBefore(threshold) && deadline.isAfter(OffsetDateTime.now());
                })
                .forEach(e -> {
                    var f        = e.getFlight();
                    var hours    = Duration.between(OffsetDateTime.now(), f.getCancellationDeadline()).toHours();
                    var title    = "⚠ Cancelación gratuita vence pronto";
                    var body     = String.format("La cancelación gratuita de %s vence en %dh",
                        e.getTitle(), hours);
                    log.info("cancellation.alert tripId={} eventId={}", trip.getId(), e.getId());
                    sender.sendPush(trip.getOwnerId(), title, body,
                        Map.of("tripId", trip.getId().toString(), "eventId", e.getId().toString(), "type", "CANCELLATION_DEADLINE"));
                    sender.sendInApp(trip.getOwnerId(), NotificationType.CANCELLATION_DEADLINE,
                        title, body, trip.getId(), e.getId(), null);
                });

            // Hoteles
            tripEvents.stream()
                .filter(e -> e.getType() == EventType.ACCOMMODATION && e.getAccommodation() != null)
                .filter(e -> {
                    var deadline = e.getAccommodation().getFreeCancellationUntil();
                    return deadline != null && deadline.isBefore(threshold) && deadline.isAfter(OffsetDateTime.now());
                })
                .forEach(e -> {
                    var a     = e.getAccommodation();
                    var hours = Duration.between(OffsetDateTime.now(), a.getFreeCancellationUntil()).toHours();
                    var title = "🏨 Cancelación gratuita de " + a.getName();
                    var body  = "Vence en " + hours + "h. Después: penalización de " +
                        (a.getCancellationPenalty() != null ? a.getCancellationPenalty() + "€" : "aplicable");
                    sender.sendPush(trip.getOwnerId(), title, body,
                        Map.of("tripId", trip.getId().toString(), "eventId", e.getId().toString()));
                    sender.sendInApp(trip.getOwnerId(), NotificationType.CANCELLATION_DEADLINE,
                        title, body, trip.getId(), e.getId(), null);
                });
        });
    }
}
