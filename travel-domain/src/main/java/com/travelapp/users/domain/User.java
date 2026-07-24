package com.travelapp.users.domain;
import com.travelapp.shared.domain.AggregateRoot;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Builder @AllArgsConstructor
public class User extends AggregateRoot<UUID> {
    private final UUID   id;
    private final String keycloakId;
    private String       email;
    private String       name;
    private String       avatarUrl;
    private UserPlan     plan;
    private OffsetDateTime planExpiresAt;
    private UserPreferences preferences;

    public void updateProfile(String name, String avatarUrl) {
        this.name = name; this.avatarUrl = avatarUrl;
    }
    public void updatePreferences(UserPreferences prefs) { this.preferences = prefs; }
    public boolean isPremium() { return plan == UserPlan.PREMIUM && (planExpiresAt == null || planExpiresAt.isAfter(OffsetDateTime.now())); }
}
