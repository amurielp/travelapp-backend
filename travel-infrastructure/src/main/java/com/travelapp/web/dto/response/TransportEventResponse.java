package com.travelapp.web.dto.response;
import com.travelapp.events.domain.TransportDetail;
import lombok.*;

@Getter @Setter @NoArgsConstructor
public class TransportEventResponse extends EventResponse {
    private TransportDetail transport;
}
