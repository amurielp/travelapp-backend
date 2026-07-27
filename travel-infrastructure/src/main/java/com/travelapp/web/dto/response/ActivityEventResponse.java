package com.travelapp.web.dto.response;
import com.travelapp.events.domain.ActivityDetail;
import lombok.*;

@Getter @Setter @NoArgsConstructor
public class ActivityEventResponse extends EventResponse {
    private ActivityDetail activity;
}
