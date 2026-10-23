package com.computemesh.coordinator.dto;

import java.util.UUID;

public record JobClaimResponse(
        UUID id,
        String type,
        String status,
        String inputData
) {
}