package com.travelapp.persistence.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelapp.documents.domain.*;
import com.travelapp.persistence.entities.TravelDocumentEntity;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class DocumentMapper {

    @Autowired
    protected ObjectMapper objectMapper;

    @Mapping(target = "parseStatus", expression = "java(doc.getParseStatus().name())")
    @Mapping(target = "parsedData",  expression = "java(toJson(doc.getParsedData()))")
    public abstract TravelDocumentEntity toEntity(TravelDocument doc);

    @Mapping(target = "parseStatus", expression = "java(com.travelapp.documents.domain.ParseStatus.valueOf(entity.getParseStatus()))")
    @Mapping(target = "parsedData",  expression = "java(fromJson(entity.getParsedData()))")
    public abstract TravelDocument toDomain(TravelDocumentEntity entity);

    protected String toJson(Object value) {
        if (value == null) return null;
        try { return objectMapper.writeValueAsString(value); }
        catch (Exception e) { return null; }
    }

    protected Object fromJson(String json) {
        if (json == null || json.isBlank()) return null;
        try { return objectMapper.readValue(json, Object.class); }
        catch (Exception e) { return null; }
    }
}
