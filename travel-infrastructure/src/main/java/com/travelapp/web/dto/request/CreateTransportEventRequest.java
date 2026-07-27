package com.travelapp.web.dto.request;
import com.travelapp.events.domain.TransportDetail;
import lombok.*;

@Getter @Setter @NoArgsConstructor
public class CreateTransportEventRequest extends CreateEventRequest {
    private TransportDetail transport;
}
