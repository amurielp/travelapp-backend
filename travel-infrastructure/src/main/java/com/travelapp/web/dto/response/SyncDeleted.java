package com.travelapp.web.dto.response;

import java.util.List;
import java.util.UUID;

public record SyncDeleted(
    List<UUID> tripIds,
    List<UUID> budgetItemIds,
    List<UUID> wishlistItemIds
) {}
