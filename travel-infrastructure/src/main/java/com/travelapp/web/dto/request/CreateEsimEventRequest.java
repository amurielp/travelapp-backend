package com.travelapp.web.dto.request;

import com.travelapp.events.domain.EsimDetail;
import lombok.*;

@Getter @Setter @NoArgsConstructor
public class CreateEsimEventRequest extends CreateEventRequest {
    private EsimDetail esim;
}
