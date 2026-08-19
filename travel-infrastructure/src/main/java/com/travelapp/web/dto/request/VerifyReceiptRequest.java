package com.travelapp.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record VerifyReceiptRequest(
        @NotBlank String store,
        @NotBlank String receiptData,
        String productId
) {}
