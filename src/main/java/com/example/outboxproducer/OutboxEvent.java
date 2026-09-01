package com.example.outboxproducer;
import jakarta.persistence.*;import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_events")public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "kafka_key", nullable = false)
    private String kafkaKey;

    @Column(nullable = false)
    private String payload;

    @Column(nullable = false)
    private String status; // PENDING, PROCESSED

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public OutboxEvent() {}

    public OutboxEvent(Long eventId, String kafkaKey, String payload) {
        this.eventId = eventId;
        this.kafkaKey = kafkaKey;
        this.payload = payload;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getEventId() { return eventId; }
    public String getKafkaKey() { return kafkaKey; }
    public String getPayload() { return payload; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}