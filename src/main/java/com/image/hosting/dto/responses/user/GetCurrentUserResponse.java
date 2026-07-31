package com.image.hosting.dto.responses.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record GetCurrentUserResponse(
        @Schema(example = "b99874bb-02b2-4c45-a3c8-a522b78e2026")
        UUID id,

        @Schema(example = "Alex Collin")
        String name,

        @Schema(example = "alex_collin@example.com")
        String email,

        @Schema(example = "2026-07-28T21:23:48.10495")
        @JsonProperty("created_at")
        LocalDateTime createdAt
) {
}
