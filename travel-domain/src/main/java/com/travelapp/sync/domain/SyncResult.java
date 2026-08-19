package com.travelapp.sync.domain;

import com.travelapp.trips.domain.Trip;
import com.travelapp.budget.domain.BudgetItem;
import com.travelapp.wishlist.domain.WishlistItem;
import lombok.Builder;
import lombok.Getter;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class SyncResult {
    private OffsetDateTime syncedAt;
    private List<Trip> updatedTrips;
    private List<BudgetItem> updatedBudgetItems;
    private List<WishlistItem> updatedWishlistItems;
    private List<UUID> deletedTripIds;
    private List<UUID> deletedBudgetItemIds;
    private List<UUID> deletedWishlistItemIds;
}
