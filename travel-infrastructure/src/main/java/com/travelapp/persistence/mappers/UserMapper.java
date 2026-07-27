package com.travelapp.persistence.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelapp.persistence.entities.UserEntity;
import com.travelapp.users.domain.*;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Autowired
    protected ObjectMapper objectMapper;

    @Mapping(target = "plan",        expression = "java(user.getPlan().name())")
    @Mapping(target = "preferences", expression = "java(toJson(user.getPreferences()))")
    @Mapping(target = "createdAt",   ignore = true)
    @Mapping(target = "updatedAt",   ignore = true)
    public abstract UserEntity toEntity(User user);

    @Mapping(target = "plan",        expression = "java(com.travelapp.users.domain.UserPlan.valueOf(entity.getPlan()))")
    @Mapping(target = "preferences", expression = "java(fromJson(entity.getPreferences()))")
    public abstract User toDomain(UserEntity entity);

    protected String toJson(UserPreferences prefs) {
        if (prefs == null) return "{}";
        try { return objectMapper.writeValueAsString(prefs); }
        catch (Exception e) { return "{}"; }
    }

    protected UserPreferences fromJson(String json) {
        if (json == null || json.isBlank()) return UserPreferences.defaults();
        try { return objectMapper.readValue(json, UserPreferences.class); }
        catch (Exception e) { return UserPreferences.defaults(); }
    }
}
