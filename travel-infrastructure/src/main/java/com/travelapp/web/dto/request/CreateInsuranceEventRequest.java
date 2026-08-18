package com.travelapp.web.dto.request;

import com.travelapp.events.domain.InsuranceDetail;
import lombok.*;

@Getter @Setter @NoArgsConstructor
public class CreateInsuranceEventRequest extends CreateEventRequest {
    private InsuranceDetail insurance;
}
