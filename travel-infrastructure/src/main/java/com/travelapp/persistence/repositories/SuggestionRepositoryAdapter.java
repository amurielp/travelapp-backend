package com.travelapp.persistence.repositories;

import com.travelapp.ai.domain.*;
import com.travelapp.ai.ports.SuggestionRepository;
import com.travelapp.persistence.entities.SuggestionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SuggestionRepositoryAdapter implements SuggestionRepository {

    private final SuggestionJpaRepository jpa;

    @Override
    public List<Suggestion> findByTripIdAndDate(UUID tripId, LocalDate date) {
        return jpa.findByTripIdAndDate(tripId, date).stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Suggestion> findById(UUID id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public Suggestion save(Suggestion suggestion) {
        return toDomain(jpa.save(toEntity(suggestion)));
    }

    @Override
    public void saveAll(List<Suggestion> suggestions) {
        jpa.saveAll(suggestions.stream().map(this::toEntity).collect(Collectors.toList()));
    }

    private Suggestion toDomain(SuggestionEntity e) {
        return Suggestion.builder()
            .id(e.getId())
            .tripId(e.getTripId())
            .date(e.getDate())
            .name(e.getName())
            .category(e.getCategory())
            .description(e.getDescription())
            .reasoning(e.getReasoning())
            .estimatedDurationMin(e.getEstimatedDurationMin() != null ? e.getEstimatedDurationMin() : 0)
            .estimatedCostEur(e.getEstimatedCostEur())
            .bestTimeOfDay(e.getBestTimeOfDay())
            .latitude(e.getLatitude())
            .longitude(e.getLongitude())
            .websiteUrl(e.getWebsiteUrl())
            .source(e.getSource())
            .status(SuggestionStatus.valueOf(e.getStatus()))
            .createdAt(e.getCreatedAt())
            .build();
    }

    private SuggestionEntity toEntity(Suggestion s) {
        return SuggestionEntity.builder()
            .id(s.getId())
            .tripId(s.getTripId())
            .date(s.getDate())
            .name(s.getName())
            .category(s.getCategory())
            .description(s.getDescription())
            .reasoning(s.getReasoning())
            .estimatedDurationMin(s.getEstimatedDurationMin())
            .estimatedCostEur(s.getEstimatedCostEur())
            .bestTimeOfDay(s.getBestTimeOfDay())
            .latitude(s.getLatitude())
            .longitude(s.getLongitude())
            .websiteUrl(s.getWebsiteUrl())
            .source(s.getSource())
            .status(s.getStatus().name())
            .createdAt(s.getCreatedAt())
            .build();
    }
}
