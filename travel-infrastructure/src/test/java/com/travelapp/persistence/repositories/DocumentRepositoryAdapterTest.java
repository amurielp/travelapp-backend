package com.travelapp.persistence.repositories;

import com.travelapp.documents.domain.*;
import com.travelapp.persistence.entities.TravelDocumentEntity;
import com.travelapp.persistence.mappers.DocumentMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentRepositoryAdapterTest {

    @Mock DocumentJpaRepository jpa;
    @Mock DocumentMapper mapper;
    @InjectMocks DocumentRepositoryAdapter adapter;

    private final UUID id = UUID.randomUUID();
    private final UUID tripId = UUID.randomUUID();
    private final UUID uploadedBy = UUID.randomUUID();

    private TravelDocument doc() {
        return TravelDocument.builder()
            .id(id).tripId(tripId).uploadedBy(uploadedBy)
            .documentTypeId("flight_ticket").displayName("IB 3456")
            .fileName("ticket.pdf").fileUrl("https://cdn/ticket.pdf")
            .fileSizeBytes(102400L).fileType("application/pdf")
            .parseStatus(ParseStatus.PENDING)
            .uploadedAt(OffsetDateTime.now()).build();
    }

    private TravelDocumentEntity entity() {
        var e = new TravelDocumentEntity();
        e.setId(id); e.setTripId(tripId); e.setUploadedBy(uploadedBy);
        e.setDocumentTypeId("flight_ticket");
        e.setFileName("ticket.pdf"); e.setFileUrl("https://cdn/ticket.pdf");
        e.setParseStatus("PENDING");
        return e;
    }

    @Test
    void save_mapsAndSaves() {
        var d = doc();
        var e = entity();
        when(mapper.toEntity(d)).thenReturn(e);
        when(jpa.save(e)).thenReturn(e);
        when(mapper.toDomain(e)).thenReturn(d);
        assertThat(adapter.save(d)).isEqualTo(d);
    }

    @Test
    void findById_found() {
        var e = entity();
        var d = doc();
        when(jpa.findById(id)).thenReturn(Optional.of(e));
        when(mapper.toDomain(e)).thenReturn(d);
        assertThat(adapter.findById(id)).contains(d);
    }

    @Test
    void findById_notFound_returnsEmpty() {
        when(jpa.findById(id)).thenReturn(Optional.empty());
        assertThat(adapter.findById(id)).isEmpty();
    }

    @Test
    void findByTripId_returnsSortedList() {
        var e = entity();
        var d = doc();
        when(jpa.findByTripIdOrderByUploadedAtDesc(tripId)).thenReturn(List.of(e));
        when(mapper.toDomain(e)).thenReturn(d);
        assertThat(adapter.findByTripId(tripId)).containsExactly(d);
    }

    @Test
    void findByTripIdAndType_filtersCorrectly() {
        var e = entity();
        var d = doc();
        when(jpa.findByTripIdAndDocumentTypeIdOrderByUploadedAtDesc(tripId, "flight_ticket"))
            .thenReturn(List.of(e));
        when(mapper.toDomain(e)).thenReturn(d);
        assertThat(adapter.findByTripIdAndType(tripId, "flight_ticket")).containsExactly(d);
    }

    @Test
    void deleteById_callsJpa() {
        adapter.deleteById(id);
        verify(jpa).deleteById(id);
    }
}
