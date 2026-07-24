package com.travelapp.wishlist.usecases;
import java.math.BigDecimal;
import java.util.UUID;
public record AddToWishlistCommand(UUID tripId, String name, String category, String destinationCity, Double latitude, Double longitude, String externalPlaceId, int priority, BigDecimal estimatedCost, String websiteUrl) {}
