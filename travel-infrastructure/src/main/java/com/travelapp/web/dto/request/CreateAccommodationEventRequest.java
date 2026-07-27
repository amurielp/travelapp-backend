package com.travelapp.web.dto.request;
import com.travelapp.events.domain.AccommodationDetail;
import lombok.*;

@Getter @Setter @NoArgsConstructor
public class CreateAccommodationEventRequest extends CreateEventRequest {
    private AccommodationDetail accommodation;
}
