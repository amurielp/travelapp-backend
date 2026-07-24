package com.travelapp.web.dto.request;

public record CreatePaymentMethodRequest(
    String name,
    String type,
    String notes
) {}
