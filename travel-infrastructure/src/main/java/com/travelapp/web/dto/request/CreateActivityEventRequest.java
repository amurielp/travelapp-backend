package com.travelapp.web.dto.request;
import com.travelapp.events.domain.ActivityDetail;
import lombok.*;

@Getter @Setter @NoArgsConstructor
public class CreateActivityEventRequest extends CreateEventRequest {
    private ActivityDetail activity;
}
