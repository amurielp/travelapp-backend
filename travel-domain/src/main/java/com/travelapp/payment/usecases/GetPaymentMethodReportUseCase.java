package com.travelapp.payment.usecases;

import com.travelapp.payment.domain.PaymentMethodReport;
import com.travelapp.payment.ports.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class GetPaymentMethodReportUseCase {
    private final PaymentMethodRepository repo;

    @Transactional(readOnly = true)
    public List<PaymentMethodReport> execute(UUID userId, UUID tripId) {
        return repo.getReportByUserId(userId, tripId);
    }
}
