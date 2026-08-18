package com.travelapp.notifications.usecases;

import com.travelapp.events.domain.EventStatus;
import com.travelapp.events.domain.EventType;
import com.travelapp.events.ports.EventRepository;
import com.travelapp.notifications.domain.NotificationType;
import com.travelapp.notifications.ports.NotificationSender;
import com.travelapp.trips.ports.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Map;

@Slf4j @Service @RequiredArgsConstructor
public class SendEsimInsuranceExpiryAlertUseCase {

    private final TripRepository     trips;
    private final EventRepository    events;
    private final NotificationSender sender;

    /** Llamado por el scheduler cada mañana a las 09:00 */
    @Transactional(readOnly = true)
    public void execute() {
        var now       = OffsetDateTime.now();
        var threshold = now.plusDays(7);

        trips.findAllActive().forEach(trip -> {
            events.findByTripId(trip.getId()).forEach(event -> {
                if (event.getStatus() == EventStatus.CANCELLED) return;
                if (event.getEndDatetime() == null) return;
                var end = event.getEndDatetime();
                if (end.isBefore(now) || end.isAfter(threshold)) return;

                if (event.getType() == EventType.ESIM) {
                    var title = "Tu eSIM vence pronto";
                    var body  = String.format("Tu eSIM %s vence el %s. Considera renovarla.",
                        event.getTitle(), end.toLocalDate());
                    log.info("esim.expiry.alert tripId={} eventId={}", trip.getId(), event.getId());
                    sender.sendPush(trip.getOwnerId(), title, body,
                        Map.of("tripId", trip.getId().toString(),
                               "eventId", event.getId().toString(),
                               "type", "ESIM_EXPIRY"));
                    sender.sendInApp(trip.getOwnerId(), NotificationType.ESIM_EXPIRY,
                        title, body, trip.getId(), event.getId(), null);

                } else if (event.getType() == EventType.INSURANCE) {
                    var title = "Tu seguro de viaje vence pronto";
                    var body  = String.format("Tu seguro de viaje %s vence el %s. Verifica la cobertura.",
                        event.getTitle(), end.toLocalDate());
                    log.info("insurance.expiry.alert tripId={} eventId={}", trip.getId(), event.getId());
                    sender.sendPush(trip.getOwnerId(), title, body,
                        Map.of("tripId", trip.getId().toString(),
                               "eventId", event.getId().toString(),
                               "type", "INSURANCE_EXPIRY"));
                    sender.sendInApp(trip.getOwnerId(), NotificationType.INSURANCE_EXPIRY,
                        title, body, trip.getId(), event.getId(), null);
                }
            });
        });
    }
}
