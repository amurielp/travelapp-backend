package com.travelapp.web.dto.response;

import java.util.UUID;

public record PaymentMethodResponse(
    UUID    id,
    UUID    userId,
    String  name,
    String  type,
    boolean active,
    String  notes
) {}
