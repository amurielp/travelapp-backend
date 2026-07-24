package com.travelapp.ai.domain;
import com.travelapp.events.domain.FreeSlot;
import java.time.LocalDate;
import java.util.List;
public record SuggestionRequest(
    String destinationCity, String countryCode, LocalDate date,
    List<FreeSlot> freeSlots, List<String> userInterests,
    FoodProfile foodProfile, TravelStyle travelStyle,
    List<String> alreadyInWishlist) {}
