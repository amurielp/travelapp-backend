package com.travelapp.events.domain;

import com.travelapp.shared.domain.AggregateRoot;
import lombok.*;
import java.time.*;
import java.util.UUID;

@Getter @Builder @AllArgsConstructor
public class TravelEvent extends AggregateRoot<UUID> {

    private final UUID      id;
    private final UUID      tripId;
    private UUID            documentId;   // nullable — si vino de PDF
    private EventType       type;
    private String          title;
    private String          notes;
    private String          color;
    private OffsetDateTime  startDatetime;
    private OffsetDateTime  endDatetime;
    private boolean         allDay;
    private ZoneId          timezone;
    private EventStatus     status;
    private EventSource     source;
    private String          locationName;
    private Double          latitude;
    private Double          longitude;
    private Double          originLatitude;
    private Double          originLongitude;

    // Detalle específico — solo uno estará presente según el type
    private FlightDetail        flight;
    private AccommodationDetail accommodation;
    private ActivityDetail      activity;
    private TransportDetail     transport;
    private EsimDetail          esim;
    private InsuranceDetail     insurance;

    public void confirm()  { this.status = EventStatus.CONFIRMED; }
    public void cancel()   { this.status = EventStatus.CANCELLED; }
    public void applyCoordinates(double lat, double lon) { this.latitude = lat; this.longitude = lon; }
    public void applyOriginCoordinates(double lat, double lon) { this.originLatitude = lat; this.originLongitude = lon; }

    public void update(com.travelapp.events.usecases.UpdateEventCommand cmd) {
        if (cmd.title()         != null) this.title         = cmd.title();
        if (cmd.notes()         != null) this.notes         = cmd.notes();
        if (cmd.color()         != null) this.color         = cmd.color();
        if (cmd.startDatetime() != null) this.startDatetime = cmd.startDatetime();
        if (cmd.endDatetime()   != null) this.endDatetime   = cmd.endDatetime();
        if (cmd.status()        != null) this.status        = cmd.status();
        if (cmd.locationName()  != null) this.locationName  = cmd.locationName();
        if (cmd.latitude()      != null) this.latitude      = cmd.latitude();
        if (cmd.longitude()     != null) this.longitude     = cmd.longitude();
        if (cmd.flight()        != null) this.flight        = cmd.flight();
        if (cmd.accommodation() != null) this.accommodation = cmd.accommodation();
        if (cmd.activity()      != null) this.activity      = cmd.activity();
        if (cmd.transport()     != null) this.transport     = cmd.transport();
        if (cmd.esim()          != null) this.esim          = cmd.esim();
        if (cmd.insurance()     != null) this.insurance     = cmd.insurance();
    }

    public boolean overlapsWith(TravelEvent other) {
        if (this.endDatetime == null || other.endDatetime == null) return false;
        return this.startDatetime.isBefore(other.endDatetime)
            && this.endDatetime.isAfter(other.startDatetime);
    }
}
