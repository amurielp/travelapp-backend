package com.travelapp.subscriptions.usecases;

import java.util.UUID;

public record VerifyReceiptCommand(UUID userId, String store, String receiptData, String productId) {}
