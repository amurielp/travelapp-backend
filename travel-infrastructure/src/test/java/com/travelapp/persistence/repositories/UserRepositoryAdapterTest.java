package com.travelapp.persistence.repositories;

import com.travelapp.persistence.entities.UserEntity;
import com.travelapp.persistence.mappers.UserMapper;
import com.travelapp.users.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock UserJpaRepository jpa;
    @Mock UserMapper mapper;
    @InjectMocks UserRepositoryAdapter adapter;

    private final UUID id = UUID.randomUUID();
    private final String keycloakId = "kc-123";
    private final String email = "test@example.com";

    private UserEntity entity() {
        var e = new UserEntity();
        e.setId(id); e.setKeycloakId(keycloakId);
        e.setEmail(email); e.setName("Test User");
        e.setPlan("FREE"); e.setPreferences("{}");
        return e;
    }

    private User user() {
        return User.builder().id(id).keycloakId(keycloakId)
            .email(email).name("Test User")
            .plan(UserPlan.FREE).preferences(UserPreferences.defaults()).build();
    }

    @Test
    void save_mapsAndSaves() {
        var u = user();
        var e = entity();
        when(mapper.toEntity(u)).thenReturn(e);
        when(jpa.save(e)).thenReturn(e);
        when(mapper.toDomain(e)).thenReturn(u);

        assertThat(adapter.save(u)).isEqualTo(u);
    }

    @Test
    void findById_found_returnsDomain() {
        var e = entity();
        var u = user();
        when(jpa.findById(id)).thenReturn(Optional.of(e));
        when(mapper.toDomain(e)).thenReturn(u);

        assertThat(adapter.findById(id)).contains(u);
    }

    @Test
    void findById_notFound_returnsEmpty() {
        when(jpa.findById(id)).thenReturn(Optional.empty());
        assertThat(adapter.findById(id)).isEmpty();
    }

    @Test
    void findByKeycloakId_found() {
        var e = entity();
        var u = user();
        when(jpa.findByKeycloakId(keycloakId)).thenReturn(Optional.of(e));
        when(mapper.toDomain(e)).thenReturn(u);

        assertThat(adapter.findByKeycloakId(keycloakId)).contains(u);
    }

    @Test
    void findByKeycloakId_notFound_returnsEmpty() {
        when(jpa.findByKeycloakId(keycloakId)).thenReturn(Optional.empty());
        assertThat(adapter.findByKeycloakId(keycloakId)).isEmpty();
    }

    @Test
    void findByEmail_delegates() {
        var e = entity();
        var u = user();
        when(jpa.findByEmail(email)).thenReturn(Optional.of(e));
        when(mapper.toDomain(e)).thenReturn(u);

        assertThat(adapter.findByEmail(email)).contains(u);
    }
}
