package com.travelapp.persistence.entities;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity @Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserEntity {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "keycloak_id", nullable = false, unique = true)
    private String keycloakId;
    @Column(nullable = false, unique = true) private String email;
    @Column(nullable = false)               private String name;
    private String avatarUrl;
    @Column(nullable = false)               private String plan;
    private OffsetDateTime planExpiresAt;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String preferences;
    @Column(updatable = false)              private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    @PrePersist void onCreate() { createdAt = updatedAt = OffsetDateTime.now(); }
    @PreUpdate  void onUpdate() { updatedAt = OffsetDateTime.now(); }
}
