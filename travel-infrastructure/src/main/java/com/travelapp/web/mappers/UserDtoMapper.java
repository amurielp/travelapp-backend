package com.travelapp.web.mappers;

import com.travelapp.users.domain.User;
import com.travelapp.users.domain.UserPreferences;
import com.travelapp.web.dto.request.UserPreferencesRequest;
import com.travelapp.web.dto.response.UserResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserDtoMapper {

    @Mapping(target = "plan", expression = "java(user.getPlan() != null ? user.getPlan().name() : null)")
    UserResponse toResponse(User user);

    @Mapping(target = "foodProfile",   ignore = true)
    @Mapping(target = "travelStyle",   ignore = true)
    @Mapping(target = "notifications", ignore = true)
    UserPreferences toDomain(UserPreferencesRequest req);
}
