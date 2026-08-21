package com.travelapp.events.usecases;

import com.travelapp.events.domain.*;
import com.travelapp.events.ports.EventRepository;
import com.travelapp.expenses.domain.Expense;
import com.travelapp.expenses.domain.ExpenseCategory;
import com.travelapp.expenses.port.ExpenseRepository;
import com.travelapp.shared.exceptions.EventNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateEventUseCase {

    private final EventRepository      eventRepository;
    private final ExpenseRepository    expenseRepository;
    private final EventGeocodingHelper geocodingHelper;

    @Transactional
    public TravelEvent execute(UpdateEventCommand cmd) {
        var event = eventRepository.findById(cmd.eventId())
            .filter(e -> e.getTripId().equals(cmd.tripId()))
            .orElseThrow(() -> new EventNotFoundException(cmd.eventId()));

        event.update(cmd);

        // Geocode destination if missing
        if (event.getLatitude() == null || event.getLongitude() == null) {
            double[] geo = geocodingHelper.resolve(
                event.getType(), null, null,
                event.getLocationName(), event.getTitle(),
                event.getFlight(), event.getAccommodation(),
                event.getActivity(), event.getTransport());
            if (geo != null) {
                event.applyCoordinates(geo[0], geo[1]);
            }
        }
        // Geocode origin if missing (FLIGHT / TRANSPORT)
        if (event.getOriginLatitude() == null || event.getOriginLongitude() == null) {
            double[] originGeo = geocodingHelper.resolveOrigin(
                event.getType(), event.getFlight(), event.getTransport());
            if (originGeo != null) {
                event.applyOriginCoordinates(originGeo[0], originGeo[1]);
            }
        }

        TravelEvent saved = eventRepository.save(event);
        syncExpense(saved);
        return saved;
    }

    private void syncExpense(TravelEvent event) {
        var price    = extractPrice(event);
        var currency = extractCurrency(event);
        if (price == null || price.compareTo(BigDecimal.ZERO) == 0) return;

        var existing = expenseRepository.findByEventId(event.getId());
        if (existing.isPresent()) {
            var exp = existing.get();
            if (exp.getAmount() == null || price.compareTo(exp.getAmount()) != 0) {
                exp.updateAmount(price);
                expenseRepository.save(exp);
            }
        } else {
            expenseRepository.save(Expense.builder()
                .id(UUID.randomUUID())
                .tripId(event.getTripId())
                .eventId(event.getId())
                .category(resolveCategory(event.getType()))
                .description(resolveDescription(event))
                .amount(price)
                .currency(currency != null ? currency : "EUR")
                .isPaid(true)
                .paidAt(OffsetDateTime.now())
                .build());
        }
    }

    private BigDecimal extractPrice(TravelEvent e) {
        return switch (e.getType()) {
            case FLIGHT        -> e.getFlight()        != null ? e.getFlight().totalPrice()          : null;
            case ACCOMMODATION -> e.getAccommodation() != null ? accommodationPrice(e)               : null;
            case ACTIVITY      -> e.getActivity()      != null ? e.getActivity().getPriceAmount()     : null;
            case TRANSPORT     -> e.getTransport()     != null ? e.getTransport().getPriceAmount()    : null;
            case ESIM          -> e.getEsim()          != null && e.getEsim().getPriceAmount() != null
                                  ? BigDecimal.valueOf(e.getEsim().getPriceAmount())      : null;
            case INSURANCE     -> e.getInsurance()     != null && e.getInsurance().getPriceAmount() != null
                                  ? BigDecimal.valueOf(e.getInsurance().getPriceAmount()) : null;
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
            case ESIM          -> e.getEsim()          != null ? e.getEsim().getPriceCurrency()          : null;
            case INSURANCE     -> e.getInsurance()     != null ? e.getInsurance().getPriceCurrency()     : null;
            default            -> null;
        };
    }

    private ExpenseCategory resolveCategory(EventType type) {
        return switch (type) {
            case FLIGHT, TRANSPORT -> ExpenseCategory.TRANSPORT;
            case ACCOMMODATION     -> ExpenseCategory.ACCOMMODATION;
            case ACTIVITY          -> ExpenseCategory.ACTIVITIES;
            default                -> ExpenseCategory.OTHER;
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
}
