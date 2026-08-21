package com.travelapp.persistence.repositories;

import com.travelapp.expenses.domain.Expense;
import com.travelapp.expenses.domain.ExpenseCategory;
import com.travelapp.expenses.port.ExpenseRepository;
import com.travelapp.persistence.entities.EventEntity;
import com.travelapp.persistence.mappers.ExpenseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ExpenseRepositoryAdapter implements ExpenseRepository {

    private final ExpenseJpaRepository expenseJpa;
    private final EventJpaRepository   eventJpa;
    private final ExpenseMapper        mapper;

    @Override
    public Expense save(Expense expense) {
        return enrich(mapper.toDomain(expenseJpa.save(mapper.toEntity(expense))));
    }

    @Override
    public Optional<Expense> findById(UUID id) {
        return expenseJpa.findByIdAndDeletedAtIsNull(id).map(mapper::toDomain).map(this::enrich);
    }

    @Override
    public Optional<Expense> findByEventId(UUID eventId) {
        return expenseJpa.findByEventIdAndDeletedAtIsNull(eventId).map(mapper::toDomain).map(this::enrich);
    }

    @Override
    public List<Expense> findByTripId(UUID tripId) {
        return expenseJpa.findByTripIdAndDeletedAtIsNull(tripId).stream()
            .map(mapper::toDomain)
            .map(this::enrich)
            .toList();
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        expenseJpa.softDeleteById(id, OffsetDateTime.now());
    }

    @Override
    public List<Expense> findByTripIdOrderByScheduledPayAt(UUID tripId) {
        return expenseJpa.findByTripIdOrderByScheduledPayAt(tripId).stream()
            .map(mapper::toDomain)
            .map(this::enrich)
            .toList();
    }

    @Override
    public List<Expense> findDueForReminder(OffsetDateTime from, OffsetDateTime to) {
        return expenseJpa.findDueForReminder(from, to).stream()
            .map(mapper::toDomain)
            .map(this::enrich)
            .toList();
    }

    @Override
    @Transactional
    public void markReminderSent(UUID id, OffsetDateTime sentAt) {
        expenseJpa.markReminderSent(id, sentAt);
    }

    @Override
    public List<ExpenseRepository.CategorySummary> getSummaryByTripId(UUID tripId) {
        return expenseJpa.getCategorySummaryRaw(tripId).stream()
            .map(row -> new ExpenseRepository.CategorySummary(
                ExpenseCategory.valueOf((String) row[0]),
                row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO,
                row[2] != null ? ((Number) row[2]).intValue() : 0,
                row[3] != null ? ((Number) row[3]).intValue() : 0
            )).toList();
    }

    // ---- helpers ----

    private Expense enrich(Expense expense) {
        if (expense.getEventId() == null) return expense;
        return eventJpa.findById(expense.getEventId())
            .map(e -> Expense.builder()
                .id(expense.getId())
                .tripId(expense.getTripId())
                .eventId(expense.getEventId())
                .category(expense.getCategory())
                .description(expense.getDescription())
                .amount(expense.getAmount())
                .currency(expense.getCurrency())
                .isPaid(expense.isPaid())
                .paidAt(expense.getPaidAt())
                .notes(expense.getNotes())
                .paymentMethodId(expense.getPaymentMethodId())
                .scheduledPayAt(expense.getScheduledPayAt())
                .reminderHoursBefore(expense.getReminderHoursBefore())
                .reminderSentAt(expense.getReminderSentAt())
                .eventTitle(e.getTitle())
                .bookingStatus(resolveBookingStatus(e))
                .build())
            .orElse(expense);
    }

    private String resolveBookingStatus(EventEntity e) {
        if (e.getFlight()        != null) return e.getFlight().getPurchaseStatus().name();
        if (e.getAccommodation() != null) return e.getAccommodation().getPurchaseStatus().name();
        if (e.getActivity()      != null) return e.getActivity().getPurchaseStatus().name();
        if (e.getTransport()     != null) return e.getTransport().getPurchaseStatus().name();
        return null;
    }
}
