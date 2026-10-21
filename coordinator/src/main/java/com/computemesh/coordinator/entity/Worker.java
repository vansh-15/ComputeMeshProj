package com.computemesh.coordinator.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
public class Worker {

    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private WorkerStatus status;

    private Instant registeredAt;

    public Worker(){

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public WorkerStatus getStatus() {
        return status;
    }

    public void setStatus(WorkerStatus status) {
        this.status = status;
    }

    public Instant getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Instant registeredAt) {
        this.registeredAt = registeredAt;
    }
}
