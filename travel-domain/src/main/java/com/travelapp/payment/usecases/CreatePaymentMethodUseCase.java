package com.travelapp.payment.usecases;

import com.travelapp.payment.domain.*;
import com.travelapp.payment.ports.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class CreatePaymentMethodUseCase {
    private final PaymentMethodRepository repo;

    @Transactional
    public PaymentMethod execute(UUID userId, String name, PaymentMethodType type, String notes) {
        return repo.save(PaymentMethod.builder()
            .id(UUID.randomUUID())
            .userId(userId)
            .name(name)
            .type(type)
            .isActive(true)
            .notes(notes)
            .sortOrder(0)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now())
            .build());
    }
}
