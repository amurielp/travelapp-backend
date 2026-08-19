package com.travelapp.persistence.repositories;

import com.travelapp.persistence.mappers.SubscriptionMapper;
import com.travelapp.subscriptions.domain.Subscription;
import com.travelapp.subscriptions.ports.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SubscriptionRepositoryAdapter implements SubscriptionRepository {

    private final SubscriptionJpaRepository jpa;
    private final SubscriptionMapper        mapper;

    @Override
    public Subscription save(Subscription sub) {
        return mapper.toDomain(jpa.save(mapper.toEntity(sub)));
    }

    @Override
    public Optional<Subscription> findActiveByUserId(UUID userId) {
        return jpa.findActiveByUserId(userId).map(mapper::toDomain);
    }
}
