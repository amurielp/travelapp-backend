package com.travelapp.web.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FlightLookupResponse {
    private String airline;
    private String flightNumber;
    private String originIata;
    private String originCity;
    private String destinationIata;
    private String destinationCity;
    private String scheduledDeparture;
    private String scheduledArrival;
    private String status;
    private String aircraftType;
}
