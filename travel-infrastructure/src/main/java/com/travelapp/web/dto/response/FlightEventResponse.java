package com.travelapp.web.dto.response;
import com.travelapp.events.domain.FlightDetail;
import lombok.*;

@Getter @Setter @NoArgsConstructor
public class FlightEventResponse extends EventResponse {
    private FlightDetail flight;
}
