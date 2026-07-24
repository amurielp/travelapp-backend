package com.travelapp.users.domain;

import org.junit.jupiter.api.Test;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class UserTest {

    private User freeUser() {
        return User.builder()
            .id(UUID.randomUUID()).keycloakId("kc-1")
            .email("u@test.com").name("User")
            .plan(UserPlan.FREE)
            .preferences(UserPreferences.defaults())
            .build();
    }

    @Test
    void isPremium_whenFree_returnsFalse() {
        assertThat(freeUser().isPremium()).isFalse();
    }

    @Test
    void isPremium_whenPremiumWithFutureExpiry_returnsTrue() {
        var user = User.builder().id(UUID.randomUUID()).keycloakId("kc-2")
            .email("p@test.com").name("Premium")
            .plan(UserPlan.PREMIUM)
            .planExpiresAt(OffsetDateTime.now().plusDays(30))
            .preferences(UserPreferences.defaults()).build();
        assertThat(user.isPremium()).isTrue();
    }

    @Test
    void isPremium_whenPremiumWithPastExpiry_returnsFalse() {
        var user = User.builder().id(UUID.randomUUID()).keycloakId("kc-3")
            .email("e@test.com").name("Expired")
            .plan(UserPlan.PREMIUM)
            .planExpiresAt(OffsetDateTime.now().minusDays(1))
            .preferences(UserPreferences.defaults()).build();
        assertThat(user.isPremium()).isFalse();
    }

    @Test
    void isPremium_whenPremiumWithNoExpiry_returnsTrue() {
        var user = User.builder().id(UUID.randomUUID()).keycloakId("kc-4")
            .email("no@test.com").name("NoExpiry")
            .plan(UserPlan.PREMIUM).planExpiresAt(null)
            .preferences(UserPreferences.defaults()).build();
        assertThat(user.isPremium()).isTrue();
    }

    @Test
    void updateProfile_changesNameAndAvatar() {
        var user = freeUser();
        user.updateProfile("New Name", "https://cdn.example.com/avatar.jpg");
        assertThat(user.getName()).isEqualTo("New Name");
        assertThat(user.getAvatarUrl()).isEqualTo("https://cdn.example.com/avatar.jpg");
    }

    @Test
    void updatePreferences_changesPrefs() {
        var user = freeUser();
        var newPrefs = UserPreferences.builder().language("en").currency("USD").build();
        user.updatePreferences(newPrefs);
        assertThat(user.getPreferences().language()).isEqualTo("en");
        assertThat(user.getPreferences().currency()).isEqualTo("USD");
    }

    @Test
    void defaultPreferences_hasCorrectDefaults() {
        var prefs = UserPreferences.defaults();
        assertThat(prefs.language()).isEqualTo("es");
        assertThat(prefs.currency()).isEqualTo("EUR");
        assertThat(prefs.budgetLevel()).isEqualTo("mid");
        assertThat(prefs.interests()).isEmpty();
    }
}
