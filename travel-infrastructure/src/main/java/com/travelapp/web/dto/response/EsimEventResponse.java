package com.travelapp.web.dto.response;

import com.travelapp.events.domain.EsimDetail;
import lombok.*;

@Getter @Setter @NoArgsConstructor
public class EsimEventResponse extends EventResponse {
    private EsimDetail esim;
}
