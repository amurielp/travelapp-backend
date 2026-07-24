package com.travelapp.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity @Table(name = "documents")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TravelDocumentEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "trip_id", nullable = false)
    private UUID tripId;

    @Column(name = "uploaded_by", nullable = false)
    private UUID uploadedBy;

    @Column(name = "document_type_id")
    private String documentTypeId;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "parse_status", nullable = false)
    private String parseStatus;

    @Column(name = "raw_extracted", columnDefinition = "text")
    private String rawExtracted;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "parsed_data", columnDefinition = "jsonb")
    private String parsedData;

    @Column(name = "parse_error")
    private String parseError;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_until")
    private LocalDate validUntil;

    private String notes;

    @Column(name = "uploaded_at", updatable = false, nullable = false)
    private OffsetDateTime uploadedAt;

    @PrePersist
    void onCreate() { if (uploadedAt == null) uploadedAt = OffsetDateTime.now(); }
}
