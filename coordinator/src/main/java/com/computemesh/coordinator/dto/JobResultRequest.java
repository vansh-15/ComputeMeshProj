package com.computemesh.coordinator.dto;

import jakarta.validation.constraints.NotBlank;

public record JobResultRequest(
        @NotBlank(message = "Result data is required")
        String resultData
) {
}
