package com.image.hosting.dto.responses.user;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public record GetCurrentUserResponse(
        UUID id,
        String name,
        String email,

        @JsonProperty("created_at")
        LocalDateTime createdAt
) {
}
