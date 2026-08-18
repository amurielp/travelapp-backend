package com.travelapp.web.mappers;

import com.travelapp.events.domain.*;
import com.travelapp.events.usecases.*;
import com.travelapp.web.dto.request.*;
import com.travelapp.web.dto.response.*;
import org.mapstruct.*;

import java.time.ZoneId;
import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventDtoMapper {

    default EventResponse toResponse(TravelEvent event) {
        EventResponse r = switch (event.getType()) {
            case FLIGHT        -> new FlightEventResponse();
            case ACCOMMODATION -> new AccommodationEventResponse();
            case ACTIVITY      -> new ActivityEventResponse();
            case TRANSPORT     -> new TransportEventResponse();
            case CUSTOM        -> new CustomEventResponse();
            case DESTINATION   -> new DestinationEventResponse();
            case ESIM          -> new EsimEventResponse();
            case INSURANCE     -> new InsuranceEventResponse();
        };
        r.setId(event.getId());
        r.setTripId(event.getTripId());
        r.setDocumentId(event.getDocumentId());
        r.setType(event.getType().name());
        r.setTitle(event.getTitle());
        r.setNotes(event.getNotes());
        r.setColor(event.getColor());
        r.setStartDatetime(event.getStartDatetime());
        r.setEndDatetime(event.getEndDatetime());
        r.setAllDay(event.isAllDay());
        r.setTimezone(event.getTimezone() != null ? event.getTimezone().getId() : null);
        r.setStatus(event.getStatus() != null ? event.getStatus().name() : null);
        r.setSource(event.getSource() != null ? event.getSource().name() : null);
        r.setLocationName(event.getLocationName());
        r.setLatitude(event.getLatitude());
        r.setLongitude(event.getLongitude());

        if (r instanceof FlightEventResponse        fr) fr.setFlight(event.getFlight());
        if (r instanceof AccommodationEventResponse ar) ar.setAccommodation(event.getAccommodation());
        if (r instanceof ActivityEventResponse      ar) ar.setActivity(event.getActivity());
        if (r instanceof TransportEventResponse     tr) tr.setTransport(event.getTransport());
        if (r instanceof EsimEventResponse          er) er.setEsim(event.getEsim());
        if (r instanceof InsuranceEventResponse     ir) ir.setInsurance(event.getInsurance());
        return r;
    }

    default CreateEventCommand toCommand(CreateEventRequest req, UUID tripId) {
        return new CreateEventCommand(
            tripId,
            null,
            EventType.valueOf(req.getType()),
            req.getTitle(),
            req.getNotes(),
            req.getColor(),
            req.getStartDatetime(),
            req.getEndDatetime(),
            req.isAllDay(),
            req.getTimezone() != null ? ZoneId.of(req.getTimezone()) : ZoneId.of("Europe/Madrid"),
            EventSource.MANUAL,
            req.getLocationName(),
            req.getLatitude(),
            req.getLongitude(),
            req instanceof CreateFlightEventRequest        r ? r.getFlight()        : null,
            req instanceof CreateAccommodationEventRequest r ? r.getAccommodation() : null,
            req instanceof CreateActivityEventRequest      r ? r.getActivity()      : null,
            req instanceof CreateTransportEventRequest     r ? r.getTransport()     : null,
            req instanceof CreateEsimEventRequest          r ? r.getEsim()          : null,
            req instanceof CreateInsuranceEventRequest     r ? r.getInsurance()     : null,
            req.getScheduledPayAt()
        );
    }

    @Mapping(target = "eventId", source = "eventId")
    @Mapping(target = "tripId",  source = "tripId")
    @Mapping(target = "status",  expression = "java(req.status() != null ? EventStatus.valueOf(req.status()) : null)")
    UpdateEventCommand toUpdateCommand(UpdateEventRequest req, UUID eventId, UUID tripId);

    @Mapping(target = "freeSlots", source = "freeSlots")
    TripDaySummaryResponse toDaySummaryResponse(TripDaySummary summary);

    default TripDaySummaryResponse.FreeSlotDto toFreeSlotDto(FreeSlot slot) {
        return new TripDaySummaryResponse.FreeSlotDto(
            slot.from().toString(), slot.to().toString());
    }
}
