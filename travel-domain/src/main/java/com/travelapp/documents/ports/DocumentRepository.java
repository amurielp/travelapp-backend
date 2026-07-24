package com.travelapp.documents.ports;

import com.travelapp.documents.domain.TravelDocument;
import java.util.*;

public interface DocumentRepository {
    TravelDocument save(TravelDocument doc);
    Optional<TravelDocument> findById(UUID id);
    List<TravelDocument> findByTripId(UUID tripId);
    List<TravelDocument> findByTripIdAndType(UUID tripId, String documentTypeId);
    void deleteById(UUID id);
}
