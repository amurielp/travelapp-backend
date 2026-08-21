package com.travelapp.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity @Table(name = "expenses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExpenseEntity {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "trip_id", nullable = false)         private UUID tripId;
    @Column(name = "event_id")                          private UUID eventId;
    @Column(nullable = false)                           private String category;
    @Column(nullable = false)                           private String description;
    @Column(name = "amount", nullable = false)           private BigDecimal amount;
    @Column(nullable = false)                           private String currency;
    @Column(name = "is_paid", nullable = false)         private boolean isPaid;
    @Column(name = "paid_at")                           private OffsetDateTime paidAt;
    private String notes;
    @Column(name = "payment_method_id")                 private UUID paymentMethodId;
    @Column(name = "scheduled_pay_at")                  private OffsetDateTime scheduledPayAt;
    @Column(name = "reminder_hours_before")             private Integer reminderHoursBefore;
    @Column(name = "reminder_sent_at")                  private OffsetDateTime reminderSentAt;
    @Column(updatable = false)                          private OffsetDateTime createdAt;
    @Column(name = "updated_at")                        private OffsetDateTime updatedAt;
    @Column(name = "deleted_at")                        private OffsetDateTime deletedAt;

    @PrePersist void onCreate() { createdAt = updatedAt = OffsetDateTime.now(); }
    @PreUpdate  void onUpdate() { updatedAt = OffsetDateTime.now(); }
}
