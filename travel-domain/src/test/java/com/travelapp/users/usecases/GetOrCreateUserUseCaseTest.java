package com.travelapp.users.usecases;

import com.travelapp.users.domain.*;
import com.travelapp.users.ports.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetOrCreateUserUseCaseTest {

    @Mock UserRepository users;
    @InjectMocks GetOrCreateUserUseCase useCase;

    private final String keycloakId = "kc-abc-123";
    private final String email = "user@example.com";
    private final String name = "Jane Doe";

    @Test
    void execute_existingUser_returnsWithoutCreating() {
        var existing = User.builder().id(UUID.randomUUID()).keycloakId(keycloakId)
            .email(email).name(name).plan(UserPlan.PREMIUM).build();
        when(users.findByKeycloakId(keycloakId)).thenReturn(Optional.of(existing));

        var result = useCase.execute(keycloakId, email, name);

        assertThat(result).isEqualTo(existing);
        verify(users, never()).save(any());
    }

    @Test
    void execute_newUser_savesAndReturns() {
        when(users.findByKeycloakId(keycloakId)).thenReturn(Optional.empty());
        when(users.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(keycloakId, email, name);

        assertThat(result.getKeycloakId()).isEqualTo(keycloakId);
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getPlan()).isEqualTo(UserPlan.FREE);
        verify(users).save(any(User.class));
    }

    @Test
    void execute_newUser_setsDefaultPreferences() {
        when(users.findByKeycloakId(keycloakId)).thenReturn(Optional.empty());
        when(users.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = useCase.execute(keycloakId, email, name);

        assertThat(result.getPreferences()).isNotNull();
        assertThat(result.getPreferences().currency()).isEqualTo("EUR");
    }
}
