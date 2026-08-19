package com.travelapp.persistence.repositories;

import com.travelapp.budget.domain.*;
import com.travelapp.budget.ports.BudgetRepository;
import com.travelapp.persistence.entities.*;
import com.travelapp.persistence.mappers.BudgetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class BudgetRepositoryAdapter implements BudgetRepository {

    private final BudgetJpaRepository     budgetJpa;
    private final BudgetItemJpaRepository itemJpa;
    private final EventJpaRepository      eventJpa;
    private final BudgetMapper            mapper;

    @Override
    public Budget save(Budget budget) {
        BudgetEntity saved = budgetJpa.save(mapper.toEntity(budget));
        return loadWithItems(saved);
    }

    @Override
    public Optional<Budget> findByTripId(UUID tripId) {
        return budgetJpa.findByTripId(tripId).map(this::loadWithItems);
    }

    @Override
    public BudgetItem saveItem(BudgetItem item) {
        return enrich(mapper.itemToDomain(itemJpa.save(mapper.itemToEntity(item))));
    }

    @Override
    public Optional<BudgetItem> findItemById(UUID id) {
        return itemJpa.findByIdAndDeletedAtIsNull(id).map(mapper::itemToDomain).map(this::enrich);
    }

    @Override
    @Transactional
    public void deleteItem(UUID id) { itemJpa.softDeleteById(id, OffsetDateTime.now()); }

    @Override
    public List<CategorySummary> getSummaryByTripId(UUID tripId) {
        return itemJpa.getCategorySummaryRaw(tripId).stream()
            .map(row -> new CategorySummary(
                BudgetCategory.valueOf((String) row[0]),
                null,
                row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO,
                row[2] != null ? new BigDecimal(row[2].toString()) : BigDecimal.ZERO,
                row[3] != null ? ((Number) row[3]).intValue() : 0,
                row[4] != null ? ((Number) row[4]).intValue() : 0
            )).toList();
    }

    @Override
    public List<BudgetItem> findItemsByTripIdOrderByScheduledPayAt(UUID tripId) {
        return itemJpa.findByTripIdOrderByScheduledPayAt(tripId).stream()
            .map(mapper::itemToDomain)
            .map(this::enrich)
            .toList();
    }

    @Override
    public List<BudgetItem> findItemsDueForReminder(OffsetDateTime from, OffsetDateTime to) {
        return itemJpa.findDueForReminder(from, to).stream()
            .map(mapper::itemToDomain)
            .map(this::enrich)
            .toList();
    }

    @Override
    @Transactional
    public void markReminderSent(UUID itemId, OffsetDateTime sentAt) {
        itemJpa.markReminderSent(itemId, sentAt);
    }

    // ---- helpers ----

    private Budget loadWithItems(BudgetEntity entity) {
        List<BudgetItem> items = itemJpa.findByBudgetIdAndDeletedAtIsNull(entity.getId())
            .stream().map(mapper::itemToDomain).map(this::enrich).toList();
        return Budget.builder()
            .id(entity.getId())
            .tripId(entity.getTripId())
            .currency(entity.getCurrency())
            .totalLimit(entity.getTotalLimit())
            .items(new ArrayList<>(items))
            .build();
    }

    private BudgetItem enrich(BudgetItem item) {
        if (item.getEventId() == null) return item;
        return eventJpa.findById(item.getEventId())
            .map(e -> BudgetItem.builder()
                .id(item.getId())
                .budgetId(item.getBudgetId())
                .eventId(item.getEventId())
                .category(item.getCategory())
                .description(item.getDescription())
                .amountEstimated(item.getAmountEstimated())
                .amountActual(item.getAmountActual())
                .currency(item.getCurrency())
                .isPaid(item.isPaid())
                .paidAt(item.getPaidAt())
                .notes(item.getNotes())
                .paymentMethodId(item.getPaymentMethodId())
                .scheduledPayAt(item.getScheduledPayAt())
                .reminderHoursBefore(item.getReminderHoursBefore())
                .reminderSentAt(item.getReminderSentAt())
                .eventTitle(e.getTitle())
                .bookingStatus(resolveBookingStatus(e))
                .build())
            .orElse(item);
    }

    private String resolveBookingStatus(EventEntity e) {
        if (e.getFlight()        != null) return e.getFlight().getPurchaseStatus().name();
        if (e.getAccommodation() != null) return e.getAccommodation().getPurchaseStatus().name();
        if (e.getActivity()      != null) return e.getActivity().getPurchaseStatus().name();
        if (e.getTransport()     != null) return e.getTransport().getPurchaseStatus().name();
        return null;
    }
}
