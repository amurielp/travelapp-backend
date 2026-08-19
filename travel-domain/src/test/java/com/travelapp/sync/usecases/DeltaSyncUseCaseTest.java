package com.travelapp.sync.usecases;

import com.travelapp.sync.domain.SyncResult;
import com.travelapp.sync.ports.SyncRepository;
import com.travelapp.trips.domain.Trip;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeltaSyncUseCaseTest {

    @Mock SyncRepository syncRepository;
    @InjectMocks DeltaSyncUseCase useCase;

    private final UUID userId = UUID.randomUUID();
    private final OffsetDateTime since = OffsetDateTime.now().minusHours(1);

    @Test
    void execute_syncedAtIsNotNullAndIsRecent() {
        stubEmptyRepo();
        OffsetDateTime before = OffsetDateTime.now().minusSeconds(1);

        SyncResult result = useCase.execute(userId, since);

        assertThat(result.getSyncedAt()).isNotNull();
        assertThat(result.getSyncedAt()).isAfter(before);
    }

    @Test
    void execute_updatedTripsAreThoseReturnedByRepo() {
        Trip trip = Trip.builder().id(UUID.randomUUID()).ownerId(userId).title("Beach").build();
        when(syncRepository.findTripsUpdatedSince(eq(userId), any())).thenReturn(List.of(trip));
        when(syncRepository.findTripIdsDeletedSince(eq(userId), any())).thenReturn(Collections.emptyList());
        when(syncRepository.findBudgetItemsUpdatedSince(eq(userId), any())).thenReturn(Collections.emptyList());
        when(syncRepository.findBudgetItemIdsDeletedSince(eq(userId), any())).thenReturn(Collections.emptyList());
        when(syncRepository.findWishlistItemsUpdatedSince(eq(userId), any())).thenReturn(Collections.emptyList());
        when(syncRepository.findWishlistItemIdsDeletedSince(eq(userId), any())).thenReturn(Collections.emptyList());

        SyncResult result = useCase.execute(userId, since);

        assertThat(result.getUpdatedTrips()).containsExactly(trip);
    }

    @Test
    void execute_deletedTripIdsAreThoseReturnedByRepo() {
        UUID deletedId = UUID.randomUUID();
        when(syncRepository.findTripsUpdatedSince(eq(userId), any())).thenReturn(Collections.emptyList());
        when(syncRepository.findTripIdsDeletedSince(eq(userId), any())).thenReturn(List.of(deletedId));
        when(syncRepository.findBudgetItemsUpdatedSince(eq(userId), any())).thenReturn(Collections.emptyList());
        when(syncRepository.findBudgetItemIdsDeletedSince(eq(userId), any())).thenReturn(Collections.emptyList());
        when(syncRepository.findWishlistItemsUpdatedSince(eq(userId), any())).thenReturn(Collections.emptyList());
        when(syncRepository.findWishlistItemIdsDeletedSince(eq(userId), any())).thenReturn(Collections.emptyList());

        SyncResult result = useCase.execute(userId, since);

        assertThat(result.getDeletedTripIds()).containsExactly(deletedId);
    }

    @Test
    void execute_emptyRepos_returnsEmptyListsNotNull() {
        stubEmptyRepo();

        SyncResult result = useCase.execute(userId, since);

        assertThat(result.getUpdatedTrips()).isNotNull().isEmpty();
        assertThat(result.getDeletedTripIds()).isNotNull().isEmpty();
        assertThat(result.getUpdatedBudgetItems()).isNotNull().isEmpty();
        assertThat(result.getDeletedBudgetItemIds()).isNotNull().isEmpty();
    }

    private void stubEmptyRepo() {
        when(syncRepository.findTripsUpdatedSince(eq(userId), any())).thenReturn(Collections.emptyList());
        when(syncRepository.findTripIdsDeletedSince(eq(userId), any())).thenReturn(Collections.emptyList());
        when(syncRepository.findBudgetItemsUpdatedSince(eq(userId), any())).thenReturn(Collections.emptyList());
        when(syncRepository.findBudgetItemIdsDeletedSince(eq(userId), any())).thenReturn(Collections.emptyList());
        when(syncRepository.findWishlistItemsUpdatedSince(eq(userId), any())).thenReturn(Collections.emptyList());
        when(syncRepository.findWishlistItemIdsDeletedSince(eq(userId), any())).thenReturn(Collections.emptyList());
    }
}
