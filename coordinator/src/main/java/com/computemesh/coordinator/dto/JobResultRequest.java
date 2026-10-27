package com.computemesh.coordinator.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record JobResultRequest(
        @NotNull(message = "Worker ID is required")
        UUID workerId,

        @Min(value = 1, message = "Attempt number must be at least 1")
        int attemptNumber,

        @NotBlank(message = "Result data is required")
        String resultData
) {}