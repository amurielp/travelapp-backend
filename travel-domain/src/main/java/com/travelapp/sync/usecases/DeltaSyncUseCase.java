package com.travelapp.sync.usecases;

import com.travelapp.sync.domain.SyncResult;
import com.travelapp.sync.ports.SyncRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeltaSyncUseCase {

    private final SyncRepository syncRepository;

    @Transactional(readOnly = true)
    public SyncResult execute(UUID userId, OffsetDateTime since) {
        return SyncResult.builder()
            .syncedAt(OffsetDateTime.now())
            .updatedTrips(syncRepository.findTripsUpdatedSince(userId, since))
            .deletedTripIds(syncRepository.findTripIdsDeletedSince(userId, since))
            .updatedExpenses(syncRepository.findExpensesUpdatedSince(userId, since))
            .deletedExpenseIds(syncRepository.findExpenseIdsDeletedSince(userId, since))
            .updatedWishlistItems(syncRepository.findWishlistItemsUpdatedSince(userId, since))
            .deletedWishlistItemIds(syncRepository.findWishlistItemIdsDeletedSince(userId, since))
            .build();
    }
}
