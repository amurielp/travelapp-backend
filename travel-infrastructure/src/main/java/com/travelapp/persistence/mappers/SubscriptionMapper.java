package com.travelapp.persistence.mappers;

import com.travelapp.subscriptions.domain.Subscription;
import com.travelapp.persistence.entities.SubscriptionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    SubscriptionEntity toEntity(Subscription sub);

    Subscription toDomain(SubscriptionEntity entity);
}
