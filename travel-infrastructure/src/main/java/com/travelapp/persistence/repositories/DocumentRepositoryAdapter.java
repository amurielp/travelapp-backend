package com.travelapp.persistence.repositories;

import com.travelapp.documents.domain.TravelDocument;
import com.travelapp.documents.ports.DocumentRepository;
import com.travelapp.persistence.mappers.DocumentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class DocumentRepositoryAdapter implements DocumentRepository {

    private final DocumentJpaRepository jpa;
    private final DocumentMapper        mapper;

    @Override
    public TravelDocument save(TravelDocument doc) {
        return mapper.toDomain(jpa.save(mapper.toEntity(doc)));
    }

    @Override
    public Optional<TravelDocument> findById(UUID id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<TravelDocument> findByTripId(UUID tripId) {
        return jpa.findByTripIdOrderByUploadedAtDesc(tripId)
                  .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<TravelDocument> findByTripIdAndType(UUID tripId, String documentTypeId) {
        return jpa.findByTripIdAndDocumentTypeIdOrderByUploadedAtDesc(tripId, documentTypeId)
                  .stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) { jpa.deleteById(id); }
}
