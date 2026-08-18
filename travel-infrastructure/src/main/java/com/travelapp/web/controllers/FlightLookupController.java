package com.travelapp.web.controllers;

import com.travelapp.web.clients.AeroDataBoxService;
import com.travelapp.web.dto.response.FlightLookupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/flights")
@RequiredArgsConstructor
public class FlightLookupController {

    private final AeroDataBoxService aeroDataBox;

    @GetMapping("/lookup")
    public ResponseEntity<FlightLookupResponse> lookup(
            @RequestParam String flightNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal Jwt jwt) {

        if (!aeroDataBox.isEnabled()) {
            return ResponseEntity.ok(FlightLookupResponse.builder()
                .airline("Iberia (DEMO)")
                .flightNumber(flightNumber.toUpperCase())
                .originIata("MAD")
                .originCity("Madrid")
                .destinationIata("BCN")
                .destinationCity("Barcelona")
                .scheduledDeparture(date + "T08:00:00Z")
                .scheduledArrival(date + "T09:05:00Z")
                .status("Scheduled")
                .aircraftType("A320")
                .build());
        }

        FlightLookupResponse result = aeroDataBox.lookup(flightNumber, date);
        if (result == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(result);
    }
}
