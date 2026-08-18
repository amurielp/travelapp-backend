package com.travelapp.web.dto.response;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = FlightEventResponse.class,        name = "FLIGHT"),
    @JsonSubTypes.Type(value = AccommodationEventResponse.class, name = "ACCOMMODATION"),
    @JsonSubTypes.Type(value = ActivityEventResponse.class,      name = "ACTIVITY"),
    @JsonSubTypes.Type(value = TransportEventResponse.class,     name = "TRANSPORT"),
    @JsonSubTypes.Type(value = CustomEventResponse.class,        name = "CUSTOM"),
    @JsonSubTypes.Type(value = DestinationEventResponse.class,   name = "DESTINATION"),
    @JsonSubTypes.Type(value = EsimEventResponse.class,          name = "ESIM"),
    @JsonSubTypes.Type(value = InsuranceEventResponse.class,     name = "INSURANCE"),
})
public abstract class EventResponse {
    protected UUID           id;
    protected UUID           tripId;
    protected UUID           documentId;
    protected String         type;
    protected String         title;
    protected String         notes;
    protected String         color;
    protected OffsetDateTime startDatetime;
    protected OffsetDateTime endDatetime;
    protected boolean        allDay;
    protected String         timezone;
    protected String         status;
    protected String         source;
    protected String         locationName;
    protected Double         latitude;
    protected Double         longitude;
}
