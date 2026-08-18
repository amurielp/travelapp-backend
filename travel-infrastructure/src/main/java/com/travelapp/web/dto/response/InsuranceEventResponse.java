package com.travelapp.web.dto.response;

import com.travelapp.events.domain.InsuranceDetail;
import lombok.*;

@Getter @Setter @NoArgsConstructor
public class InsuranceEventResponse extends EventResponse {
    private InsuranceDetail insurance;
}
