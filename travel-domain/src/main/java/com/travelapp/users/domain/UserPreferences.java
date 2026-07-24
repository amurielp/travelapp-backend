package com.travelapp.users.domain;
import com.travelapp.ai.domain.FoodProfile;
import com.travelapp.ai.domain.TravelStyle;
import lombok.Builder;
import java.util.List;

@Builder
public record UserPreferences(
    List<String> interests,
    FoodProfile  foodProfile,
    TravelStyle  travelStyle,
    String       budgetLevel,
    String       language,
    String       currency,
    NotificationPrefs notifications
) {
    public static UserPreferences defaults() {
        return UserPreferences.builder()
            .interests(List.of())
            .budgetLevel("mid")
            .language("es")
            .currency("EUR")
            .build();
    }
}
