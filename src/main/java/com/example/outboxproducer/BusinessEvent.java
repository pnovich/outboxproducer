package com.example.outboxproducer;
import jakarta.persistence.*;import java.time.LocalDateTime;

@Entity
@Table(name = "business_events")public class BusinessEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String payload;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public BusinessEvent() {}

    public BusinessEvent(String payload) {
        this.payload = payload;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getPayload() { return payload; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}