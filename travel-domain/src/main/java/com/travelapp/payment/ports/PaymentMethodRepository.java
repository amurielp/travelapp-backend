package com.travelapp.payment.ports;

import com.travelapp.payment.domain.*;
import java.util.*;

public interface PaymentMethodRepository {
    PaymentMethod save(PaymentMethod method);
    Optional<PaymentMethod> findById(UUID id);
    List<PaymentMethod> findByUserId(UUID userId);
    List<PaymentMethod> findActiveByUserId(UUID userId);
    void deleteById(UUID id);
    List<PaymentMethodReport> getReportByUserId(UUID userId, UUID tripId);
}
