package com.computemesh.worker_agent.model;

import java.time.Instant;
import java.util.UUID;

public record WorkerRegistrationResponse(
    UUID id,
    String status,
    Instant registeredAt
){

}
