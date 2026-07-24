package com.travelapp.web.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserResponse(
    UUID           id,
    String         email,
    String         name,
    String         avatarUrl,
    String         plan,
    OffsetDateTime planExpiresAt
) {}
