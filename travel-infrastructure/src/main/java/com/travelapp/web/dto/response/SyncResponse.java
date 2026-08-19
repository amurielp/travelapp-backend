package com.travelapp.web.dto.response;

import java.time.OffsetDateTime;

public record SyncResponse(
    OffsetDateTime syncedAt,
    SyncUpdated    updated,
    SyncDeleted    deleted
) {}
