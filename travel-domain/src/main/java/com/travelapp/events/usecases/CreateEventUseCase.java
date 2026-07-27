package com.travelapp.events.usecases;

import com.travelapp.budget.domain.*;
import com.travelapp.budget.ports.BudgetRepository;
import com.travelapp.events.domain.*;
import com.travelapp.events.ports.EventRepository;
import com.travelapp.shared.exceptions.DomainValidationException;
import com.travelapp.shared.exceptions.EventOverlapException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateEventUseCase {

    private final EventRepository  eventRepository;
    private final BudgetRepository budgetRepository;

    @Transactional
    public TravelEvent execute(CreateEventCommand cmd) {

        if (cmd.type() == EventType.ACCOMMODATION) {
            var acc = cmd.accommodation();
            if (acc == null || acc.getName() == null || acc.getName().isBlank()) {
                throw new DomainValidationException("El nombre del alojamiento es obligatorio.");
            }
        }

        if (cmd.endDatetime() != null
                && cmd.type() != EventType.ACCOMMODATION
                && cmd.type() != EventType.DESTINATION) {
            var overlapping = eventRepository
                .findByTripIdAndDateRange(cmd.tripId(), cmd.startDatetime().toLocalDate(),
                                          cmd.endDatetime().toLocalDate())
                .stream()
                .filter(e -> e.getType() != EventType.ACCOMMODATION)
                .filter(e -> e.overlapsWith(buildDraftEvent(cmd)))
                .toList();

            if (!overlapping.isEmpty()) {
                throw new EventOverlapException(overlapping.get(0));
            }
        }

        var event = TravelEvent.builder()
            .id(UUID.randomUUID())
            .tripId(cmd.tripId())
            .documentId(cmd.documentId())
            .type(cmd.type())
            .title(cmd.title())
            .notes(cmd.notes())
            .color(cmd.color())
            .startDatetime(cmd.startDatetime())
            .endDatetime(cmd.endDatetime())
            .allDay(cmd.allDay())
            .timezone(cmd.timezone())
            .status(EventStatus.CONFIRMED)
            .source(cmd.source() != null ? cmd.source() : EventSource.MANUAL)
            .locationName(cmd.locationName())
            .latitude(cmd.latitude())
            .longitude(cmd.longitude())
            .flight(cmd.flight())
            .accommodation(cmd.accommodation())
            .activity(cmd.activity())
            .transport(cmd.transport())
            .build();

        TravelEvent saved = eventRepository.save(event);

        autoCreateBudgetItem(saved, cmd.scheduledPayAt());

        return saved;
    }

    // ── Budget auto-creation ──────────────────────────────────

    private void autoCreateBudgetItem(TravelEvent event, OffsetDateTime scheduledPayAt) {
        var price    = extractPrice(event);
        var currency = extractCurrency(event);
        if (price == null || price.compareTo(BigDecimal.ZERO) == 0) return;

        var budget = budgetRepository.findByTripId(event.getTripId())
            .orElseGet(() -> budgetRepository.save(Budget.builder()
                .id(UUID.randomUUID())
                .tripId(event.getTripId())
                .currency(currency != null ? currency : "EUR")
                .items(new ArrayList<>())
                .build()));

        var item = BudgetItem.builder()
            .id(UUID.randomUUID())
            .budgetId(budget.getId())
            .eventId(event.getId())
            .category(resolveCategory(event.getType()))
            .description(resolveDescription(event))
            .amountEstimated(price)
            .currency(currency != null ? currency : budget.getCurrency())
            .isPaid(false)
            .scheduledPayAt(scheduledPayAt)
            .build();

        budgetRepository.saveItem(item);
    }

    private BigDecimal extractPrice(TravelEvent e) {
        return switch (e.getType()) {
            case FLIGHT        -> e.getFlight()        != null ? e.getFlight().totalPrice()          : null;
            case ACCOMMODATION -> e.getAccommodation() != null ? accommodationPrice(e)               : null;
            case ACTIVITY      -> e.getActivity()      != null ? e.getActivity().getPriceAmount()     : null;
            case TRANSPORT     -> e.getTransport()     != null ? e.getTransport().getPriceAmount()    : null;
            default            -> null;
        };
    }

    private BigDecimal accommodationPrice(TravelEvent e) {
        var a = e.getAccommodation();
        if (a.getTotalPrice() != null) return a.getTotalPrice();
        if (a.getPricePerNight() != null && e.getStartDatetime() != null && e.getEndDatetime() != null) {
            long nights = ChronoUnit.DAYS.between(
                e.getStartDatetime().toLocalDate(), e.getEndDatetime().toLocalDate());
            return a.getPricePerNight().multiply(BigDecimal.valueOf(Math.max(1, nights)));
        }
        return null;
    }

    private String extractCurrency(TravelEvent e) {
        return switch (e.getType()) {
            case FLIGHT        -> e.getFlight()        != null ? e.getFlight().getPriceCurrency()        : null;
            case ACCOMMODATION -> e.getAccommodation() != null ? e.getAccommodation().getPriceCurrency() : null;
            case ACTIVITY      -> e.getActivity()      != null ? e.getActivity().getPriceCurrency()      : null;
            case TRANSPORT     -> e.getTransport()     != null ? e.getTransport().getPriceCurrency()     : null;
            default            -> null;
        };
    }

    private BudgetCategory resolveCategory(EventType type) {
        return switch (type) {
            case FLIGHT, TRANSPORT -> BudgetCategory.TRANSPORT;
            case ACCOMMODATION     -> BudgetCategory.ACCOMMODATION;
            case ACTIVITY          -> BudgetCategory.ACTIVITIES;
            default                -> BudgetCategory.OTHER;
        };
    }

    private String resolveDescription(TravelEvent e) {
        if (e.getType() == EventType.ACCOMMODATION && e.getAccommodation() != null
                && e.getAccommodation().getName() != null)
            return e.getAccommodation().getName();
        if (e.getType() == EventType.ACTIVITY && e.getActivity() != null
                && e.getActivity().getVenueName() != null)
            return e.getActivity().getVenueName();
        return e.getTitle();
    }

    private TravelEvent buildDraftEvent(CreateEventCommand cmd) {
        return TravelEvent.builder()
            .startDatetime(cmd.startDatetime())
            .endDatetime(cmd.endDatetime())
            .build();
    }
}
