package com.travelapp.persistence.mappers;

import com.travelapp.persistence.entities.UserConsentEntity;
import com.travelapp.users.domain.UserConsent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserConsentMapper {

    UserConsentEntity toEntity(UserConsent consent);

    UserConsent toDomain(UserConsentEntity entity);
}
