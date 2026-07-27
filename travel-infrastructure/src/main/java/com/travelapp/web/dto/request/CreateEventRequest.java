package com.travelapp.web.dto.request;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CreateFlightEventRequest.class,        name = "FLIGHT"),
    @JsonSubTypes.Type(value = CreateAccommodationEventRequest.class, name = "ACCOMMODATION"),
    @JsonSubTypes.Type(value = CreateActivityEventRequest.class,      name = "ACTIVITY"),
    @JsonSubTypes.Type(value = CreateTransportEventRequest.class,     name = "TRANSPORT"),
    @JsonSubTypes.Type(value = CreateCustomEventRequest.class,        name = "CUSTOM"),
    @JsonSubTypes.Type(value = CreateDestinationEventRequest.class,   name = "DESTINATION"),
})
public abstract class CreateEventRequest {
    @NotBlank
    protected String         type;
    @NotBlank
    protected String         title;
    protected String         notes;
    protected String         color;
    @NotNull
    protected OffsetDateTime startDatetime;
    protected OffsetDateTime endDatetime;
    protected boolean        allDay;
    protected String         timezone;
    protected String         locationName;
    protected Double         latitude;
    protected Double         longitude;
    // Fecha prevista de pago — se traslada automáticamente al BudgetItem creado
    protected OffsetDateTime scheduledPayAt;
}
