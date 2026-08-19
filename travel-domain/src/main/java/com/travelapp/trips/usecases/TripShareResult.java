package com.travelapp.trips.usecases;

import java.time.OffsetDateTime;

public record TripShareResult(String deepLink, String webUrl, OffsetDateTime expiresAt) {}
