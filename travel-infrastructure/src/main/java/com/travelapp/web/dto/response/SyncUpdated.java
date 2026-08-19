package com.travelapp.web.dto.response;

import java.util.List;

public record SyncUpdated(
    List<TripResponse>       trips,
    List<BudgetItemResponse> budgetItems,
    List<WishlistItemResponse> wishlistItems
) {}
