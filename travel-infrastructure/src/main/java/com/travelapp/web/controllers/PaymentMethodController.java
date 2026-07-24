package com.travelapp.web.controllers;

import com.travelapp.payment.domain.*;
import com.travelapp.payment.ports.PaymentMethodRepository;
import com.travelapp.payment.usecases.CreatePaymentMethodUseCase;
import com.travelapp.payment.usecases.GetPaymentMethodReportUseCase;
import com.travelapp.web.dto.request.CreatePaymentMethodRequest;
import com.travelapp.web.dto.response.PaymentMethodReportResponse;
import com.travelapp.web.dto.response.PaymentMethodResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final CreatePaymentMethodUseCase    createMethod;
    private final GetPaymentMethodReportUseCase getReport;
    private final PaymentMethodRepository       repo;

    @GetMapping
    public ResponseEntity<List<PaymentMethodResponse>> listPaymentMethods(
            @AuthenticationPrincipal Jwt jwt) {
        var userId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(repo.findActiveByUserId(userId)
            .stream().map(this::toResponse).toList());
    }

    @PostMapping
    public ResponseEntity<PaymentMethodResponse> createPaymentMethod(
            @RequestBody CreatePaymentMethodRequest req,
            @AuthenticationPrincipal Jwt jwt) {
        var userId = UUID.fromString(jwt.getSubject());
        var method = createMethod.execute(userId, req.name(),
            PaymentMethodType.valueOf(req.type().toUpperCase()), req.notes());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(method));
    }

    @GetMapping("/report/{tripId}")
    public ResponseEntity<List<PaymentMethodReportResponse>> getPaymentMethodReport(
            @PathVariable UUID tripId,
            @AuthenticationPrincipal Jwt jwt) {
        var userId = UUID.fromString(jwt.getSubject());
        return ResponseEntity.ok(getReport.execute(userId, tripId)
            .stream().map(this::toReportResponse).toList());
    }

    private PaymentMethodResponse toResponse(PaymentMethod m) {
        return new PaymentMethodResponse(
            m.getId(), m.getUserId(), m.getName(),
            m.getType().name().toLowerCase(), m.isActive(), m.getNotes());
    }

    private PaymentMethodReportResponse toReportResponse(PaymentMethodReport r) {
        return new PaymentMethodReportResponse(
            r.paymentMethodId(), r.paymentMethodName(),
            r.type().name().toLowerCase(), r.currency(),
            r.totalConfirmed(), r.totalReserved(),
            r.totalPending(), r.totalAll(), r.numExpenses());
    }
}
