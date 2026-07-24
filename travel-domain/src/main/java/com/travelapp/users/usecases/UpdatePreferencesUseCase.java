package com.travelapp.users.usecases;
import com.travelapp.users.domain.*;
import com.travelapp.users.ports.UserRepository;
import com.travelapp.shared.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class UpdatePreferencesUseCase {
    private final UserRepository users;

    @Transactional
    public User execute(UUID userId, UserPreferences prefs) {
        var user = users.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        user.updatePreferences(prefs);
        return users.save(user);
    }
}
