package com.travelapp.events.ports;

public interface GeocodingPort {

    /** Geocode a free-text query (city, address, venue name…). Returns [lat, lon] or null if not found. */
    double[] geocode(String query);

    /** Resolve IATA airport code → [lat, lon], or null if not found. */
    double[] geocodeIata(String iataCode);
}
