package com.travelapp.persistence.mappers;

import com.travelapp.persistence.entities.RewardedUnlockEntity;
import com.travelapp.users.domain.RewardedUnlock;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RewardedUnlockMapper {

    RewardedUnlockEntity toEntity(RewardedUnlock unlock);

    RewardedUnlock toDomain(RewardedUnlockEntity entity);
}
