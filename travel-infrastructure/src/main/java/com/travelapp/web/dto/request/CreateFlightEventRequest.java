package com.travelapp.web.dto.request;
import com.travelapp.events.domain.FlightDetail;
import lombok.*;

@Getter @Setter @NoArgsConstructor
public class CreateFlightEventRequest extends CreateEventRequest {
    private FlightDetail flight;
}
