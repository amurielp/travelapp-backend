package com.travelapp.users.usecases;
import com.travelapp.users.domain.*;
import com.travelapp.users.ports.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class GetOrCreateUserUseCase {
    private final UserRepository users;

    @Transactional
    public User execute(String keycloakId, String email, String name) {
        return users.findByKeycloakId(keycloakId).orElseGet(() ->
            users.save(User.builder()
                .id(UUID.randomUUID())
                .keycloakId(keycloakId)
                .email(email).name(name)
                .plan(UserPlan.FREE)
                .preferences(UserPreferences.defaults())
                .build()));
    }
}
