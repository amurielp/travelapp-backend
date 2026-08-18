package com.travelapp.persistence.entities;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.UUID;

@Entity @Table(name = "events")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EventEntity {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "trip_id", nullable = false)         private UUID tripId;
    @Column(name = "document_id")                       private UUID documentId;
    @Column(nullable = false)                           private String type;
    @Column(nullable = false)                           private String title;
    private String notes;
    private String color;
    @Column(name = "start_datetime", nullable = false)  private OffsetDateTime startDatetime;
    @Column(name = "end_datetime")                      private OffsetDateTime endDatetime;
    @Column(name = "all_day")                           private boolean allDay;
    @Column(nullable = false)                           private String timezone;
    @Column(nullable = false)                           private String status;
    @Column(nullable = false)                           private String source;
    @Column(name = "location_name")                     private String locationName;
    private Double latitude;
    private Double longitude;
    @Column(updatable = false)                          private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @OneToOne(mappedBy = "event", fetch = FetchType.EAGER, optional = true)
    private FlightEntity        flight;
    @OneToOne(mappedBy = "event", fetch = FetchType.EAGER, optional = true)
    private AccommodationEntity accommodation;
    @OneToOne(mappedBy = "event", fetch = FetchType.EAGER, optional = true)
    private ActivityEntity      activity;
    @OneToOne(mappedBy = "event", fetch = FetchType.EAGER, optional = true)
    private TransportEntity     transport;
    @OneToOne(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY, optional = true)
    private EsimEntity          esim;
    @OneToOne(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY, optional = true)
    private InsuranceEntity     insurance;

    @PrePersist void onCreate() { createdAt = updatedAt = OffsetDateTime.now(); }
    @PreUpdate  void onUpdate() { updatedAt = OffsetDateTime.now(); }
}
