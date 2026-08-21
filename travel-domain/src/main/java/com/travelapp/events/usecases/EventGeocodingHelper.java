package com.travelapp.events.usecases;

import com.travelapp.events.domain.*;
import com.travelapp.events.ports.GeocodingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventGeocodingHelper {

    private final GeocodingPort geocodingPort;

    /**
     * Returns geocoded [lat, lon] for the event if the caller did not already
     * supply coordinates.  Never throws — geocoding failures are logged and
     * silently ignored so event creation is never blocked.
     */
    public double[] resolve(EventType type, Double providedLat, Double providedLon,
                            String locationName, String title,
                            FlightDetail flight, AccommodationDetail accommodation,
                            ActivityDetail activity, TransportDetail transport) {

        if (providedLat != null && providedLon != null) return null; // caller already set them

        return switch (type) {
            case FLIGHT        -> resolveForFlight(flight);
            case ACCOMMODATION -> resolveForAccommodation(accommodation);
            case ACTIVITY      -> resolveForActivity(activity);
            case TRANSPORT     -> resolveForTransport(transport);
            case DESTINATION   -> resolveForDestination(locationName, title);
            default            -> null;
        };
    }

    /** Resolves origin coordinates for FLIGHT and TRANSPORT events, null for all others. */
    public double[] resolveOrigin(EventType type, FlightDetail flight, TransportDetail transport) {
        return switch (type) {
            case FLIGHT    -> resolveFlightOrigin(flight);
            case TRANSPORT -> resolveTransportOrigin(transport);
            default        -> null;
        };
    }

    private double[] resolveForFlight(FlightDetail f) {
        if (f == null) return null;
        if (f.getDestinationIata() != null) {
            var r = geocodingPort.geocodeIata(f.getDestinationIata());
            if (r != null) return r;
        }
        if (f.getDestinationCity() != null) return geocodingPort.geocode(f.getDestinationCity());
        return null;
    }

    private double[] resolveFlightOrigin(FlightDetail f) {
        if (f == null) return null;
        if (f.getOriginIata() != null) {
            var r = geocodingPort.geocodeIata(f.getOriginIata());
            if (r != null) return r;
        }
        if (f.getOriginCity() != null) return geocodingPort.geocode(f.getOriginCity());
        return null;
    }

    private double[] resolveTransportOrigin(TransportDetail t) {
        if (t == null) return null;
        if (t.getOriginName() != null) return geocodingPort.geocode(t.getOriginName());
        return null;
    }

    private double[] resolveForAccommodation(AccommodationDetail a) {
        if (a == null) return null;
        String query = Stream.of(a.getName(), a.getCity(), a.getCountry())
            .filter(s -> s != null && !s.isBlank())
            .collect(Collectors.joining(", "));
        return query.isBlank() ? null : geocodingPort.geocode(query);
    }

    private double[] resolveForActivity(ActivityDetail a) {
        if (a == null) return null;
        String query = Stream.of(a.getVenueName(), a.getCity())
            .filter(s -> s != null && !s.isBlank())
            .collect(Collectors.joining(", "));
        return query.isBlank() ? null : geocodingPort.geocode(query);
    }

    private double[] resolveForTransport(TransportDetail t) {
        if (t == null) return null;
        // lat/lon on the event = destination; origin stored separately via resolveOrigin()
        if (t.getDestinationName() != null) return geocodingPort.geocode(t.getDestinationName());
        return null;
    }

    private double[] resolveForDestination(String locationName, String title) {
        if (locationName != null && !locationName.isBlank()) return geocodingPort.geocode(locationName);
        if (title        != null && !title.isBlank())        return geocodingPort.geocode(title);
        return null;
    }
}
