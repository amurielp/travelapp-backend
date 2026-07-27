package com.travelapp.persistence.repositories;

import com.travelapp.payment.domain.*;
import com.travelapp.payment.ports.PaymentMethodRepository;
import com.travelapp.persistence.entities.PaymentMethodEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.*;

@Repository @RequiredArgsConstructor
public class PaymentMethodRepositoryAdapter implements PaymentMethodRepository {

    private final PaymentMethodJpaRepository jpa;

    @Override
    public PaymentMethod save(PaymentMethod m) {
        return toDomain(jpa.save(toEntity(m)));
    }

    @Override
    public Optional<PaymentMethod> findById(UUID id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<PaymentMethod> findByUserId(UUID userId) {
        return jpa.findByUserIdOrderBySortOrderAscNameAsc(userId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<PaymentMethod> findActiveByUserId(UUID userId) {
        return jpa.findByUserIdAndIsActiveTrueOrderBySortOrderAscNameAsc(userId).stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) { jpa.deleteById(id); }

    @Override
    public List<PaymentMethodReport> getReportByUserId(UUID userId, UUID tripId) {
        var rows = jpa.getReportRaw(userId, tripId);
        // Agrupar por payment_method_id
        var grouped = new LinkedHashMap<UUID, List<Object[]>>();
        for (var row : rows) {
            var pmId = UUID.fromString((String) row[0]);
            grouped.computeIfAbsent(pmId, k -> new ArrayList<>()).add(row);
        }
        return grouped.entrySet().stream().map(e -> {
            var first = e.getValue().get(0);
            var lines = e.getValue().stream().map(r -> new PaymentMethodReportLine(
                (String) r[5], (String) r[6],
                r[7] != null ? new BigDecimal(r[7].toString()) : BigDecimal.ZERO,
                (String) r[8], (String) r[9],
                r[10] != null ? java.time.OffsetDateTime.parse(r[10].toString()) : null
            )).toList();
            return new PaymentMethodReport(
                e.getKey(), (String) first[1],
                PaymentMethodType.valueOf((String) first[2]),
                (String) first[8],
                sumByStatus(lines, "CONFIRMED"),
                sumByStatus(lines, "RESERVED"),
                sumByStatus(lines, "PENDING"),
                lines.stream().map(PaymentMethodReportLine::amount).reduce(BigDecimal.ZERO, BigDecimal::add),
                lines.size(), lines
            );
        }).toList();
    }

    private BigDecimal sumByStatus(List<PaymentMethodReportLine> lines, String status) {
        return lines.stream().filter(l -> status.equals(l.purchaseStatus()))
            .map(PaymentMethodReportLine::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private PaymentMethod toDomain(PaymentMethodEntity e) {
        return PaymentMethod.builder()
            .id(e.getId()).userId(e.getUserId()).name(e.getName())
            .type(PaymentMethodType.valueOf(e.getType()))
            .isActive(e.isActive()).notes(e.getNotes()).sortOrder(e.getSortOrder())
            .createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt())
            .build();
    }

    private PaymentMethodEntity toEntity(PaymentMethod m) {
        return PaymentMethodEntity.builder()
            .id(m.getId()).userId(m.getUserId()).name(m.getName())
            .type(m.getType().name()).isActive(m.isActive())
            .notes(m.getNotes()).sortOrder(m.getSortOrder())
            .build();
    }
}
