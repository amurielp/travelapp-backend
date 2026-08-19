package com.travelapp.persistence.repositories;

import com.travelapp.budget.domain.BudgetItem;
import com.travelapp.persistence.mappers.BudgetMapper;
import com.travelapp.persistence.mappers.TripMapper;
import com.travelapp.persistence.mappers.WishlistMapper;
import com.travelapp.sync.ports.SyncRepository;
import com.travelapp.trips.domain.Trip;
import com.travelapp.wishlist.domain.WishlistItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SyncRepositoryAdapter implements SyncRepository {

    private final TripJpaRepository        tripJpaRepository;
    private final BudgetItemJpaRepository  budgetItemJpaRepository;
    private final WishlistJpaRepository    wishlistJpaRepository;
    private final TripMapper               tripMapper;
    private final BudgetMapper             budgetMapper;
    private final WishlistMapper           wishlistMapper;

    @Override
    public List<Trip> findTripsUpdatedSince(UUID userId, OffsetDateTime since) {
        return tripJpaRepository.findUpdatedSince(userId, since)
            .stream().map(tripMapper::toDomain).toList();
    }

    @Override
    public List<UUID> findTripIdsDeletedSince(UUID userId, OffsetDateTime since) {
        return tripJpaRepository.findDeletedIdsSince(userId, since);
    }

    @Override
    public List<BudgetItem> findBudgetItemsUpdatedSince(UUID userId, OffsetDateTime since) {
        return budgetItemJpaRepository.findUpdatedSince(userId, since)
            .stream().map(budgetMapper::itemToDomain).toList();
    }

    @Override
    public List<UUID> findBudgetItemIdsDeletedSince(UUID userId, OffsetDateTime since) {
        return budgetItemJpaRepository.findDeletedIdsSince(userId, since);
    }

    @Override
    public List<WishlistItem> findWishlistItemsUpdatedSince(UUID userId, OffsetDateTime since) {
        return wishlistJpaRepository.findUpdatedSince(userId, since)
            .stream().map(wishlistMapper::toDomain).toList();
    }

    @Override
    public List<UUID> findWishlistItemIdsDeletedSince(UUID userId, OffsetDateTime since) {
        return wishlistJpaRepository.findDeletedIdsSince(userId, since);
    }
}
