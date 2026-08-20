package com.travelapp.persistence.repositories;

import com.travelapp.expenses.domain.Expense;
import com.travelapp.persistence.mappers.ExpenseMapper;
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

    private final TripJpaRepository     tripJpaRepository;
    private final ExpenseJpaRepository  expenseJpaRepository;
    private final WishlistJpaRepository wishlistJpaRepository;
    private final TripMapper            tripMapper;
    private final ExpenseMapper         expenseMapper;
    private final WishlistMapper        wishlistMapper;

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
    public List<Expense> findExpensesUpdatedSince(UUID userId, OffsetDateTime since) {
        return expenseJpaRepository.findUpdatedSince(userId, since)
            .stream().map(expenseMapper::toDomain).toList();
    }

    @Override
    public List<UUID> findExpenseIdsDeletedSince(UUID userId, OffsetDateTime since) {
        return expenseJpaRepository.findDeletedIdsSince(userId, since);
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
