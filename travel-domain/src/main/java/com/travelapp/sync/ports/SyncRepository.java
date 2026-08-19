package com.travelapp.sync.ports;

import com.travelapp.trips.domain.Trip;
import com.travelapp.budget.domain.BudgetItem;
import com.travelapp.wishlist.domain.WishlistItem;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface SyncRepository {
    List<Trip> findTripsUpdatedSince(UUID userId, OffsetDateTime since);
    List<UUID> findTripIdsDeletedSince(UUID userId, OffsetDateTime since);
    List<BudgetItem> findBudgetItemsUpdatedSince(UUID userId, OffsetDateTime since);
    List<UUID> findBudgetItemIdsDeletedSince(UUID userId, OffsetDateTime since);
    List<WishlistItem> findWishlistItemsUpdatedSince(UUID userId, OffsetDateTime since);
    List<UUID> findWishlistItemIdsDeletedSince(UUID userId, OffsetDateTime since);
}
