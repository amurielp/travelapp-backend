package com.travelapp.web.dto.response;
import com.travelapp.events.domain.AccommodationDetail;
import lombok.*;

@Getter @Setter @NoArgsConstructor
public class AccommodationEventResponse extends EventResponse {
    private AccommodationDetail accommodation;
}
