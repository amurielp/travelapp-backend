package com.travelapp.users.ports;
import com.travelapp.users.domain.User;
import java.util.Optional;
import java.util.UUID;
public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByKeycloakId(String keycloakId);
    Optional<User> findByEmail(String email);
}
