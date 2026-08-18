package com.travelapp.web.clients;

import com.travelapp.web.dto.response.FlightLookupResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class AeroDataBoxService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${travelapp.features.flight-lookup.enabled:false}")
    private boolean enabled;

    @Value("${travelapp.features.flight-lookup.api-key:}")
    private String apiKey;

    @Value("${travelapp.features.flight-lookup.api-host:aerodatabox.p.rapidapi.com}")
    private String apiHost;

    public boolean isEnabled() {
        return enabled;
    }

    public FlightLookupResponse lookup(String flightNumber, LocalDate date) {
        String url = "https://" + apiHost + "/flights/number/" + flightNumber + "/" + date;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", apiKey);
        headers.set("X-RapidAPI-Host", apiHost);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> req = new HttpEntity<>(headers);
        ResponseEntity<AeroDataBoxFlight[]> resp =
            restTemplate.exchange(url, HttpMethod.GET, req, AeroDataBoxFlight[].class);

        AeroDataBoxFlight[] flights = resp.getBody();
        if (flights == null || flights.length == 0) return null;

        AeroDataBoxFlight f = flights[0];
        return FlightLookupResponse.builder()
            .airline(f.airline != null ? f.airline.name : null)
            .flightNumber(flightNumber.toUpperCase())
            .originIata(f.departure != null && f.departure.airport != null ? f.departure.airport.iata : null)
            .originCity(f.departure != null && f.departure.airport != null ? f.departure.airport.municipalityName : null)
            .destinationIata(f.arrival != null && f.arrival.airport != null ? f.arrival.airport.iata : null)
            .destinationCity(f.arrival != null && f.arrival.airport != null ? f.arrival.airport.municipalityName : null)
            .scheduledDeparture(f.departure != null ? f.departure.scheduledTime : null)
            .scheduledArrival(f.arrival != null ? f.arrival.scheduledTime : null)
            .status(f.status)
            .aircraftType(f.aircraft != null ? f.aircraft.model : null)
            .build();
    }

    // ── Inner classes matching AeroDataBox JSON structure ────────────────────

    static class AeroDataBoxFlight {
        public String status;
        public Airline airline;
        public Endpoint departure;
        public Endpoint arrival;
        public Aircraft aircraft;
    }

    static class Airline {
        public String name;
    }

    static class Endpoint {
        public Airport airport;
        public String scheduledTime; // ISO datetime string from API
    }

    static class Airport {
        public String iata;
        public String municipalityName;
    }

    static class Aircraft {
        public String model;
    }
}
