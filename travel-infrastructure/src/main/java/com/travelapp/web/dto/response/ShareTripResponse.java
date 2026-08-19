package com.travelapp.web.dto.response;

import java.time.OffsetDateTime;

public record ShareTripResponse(String deepLink, String webUrl, OffsetDateTime expiresAt) {}
