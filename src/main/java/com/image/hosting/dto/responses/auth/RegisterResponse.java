package com.image.hosting.dto.responses.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record RegisterResponse(
        @Schema(example = "b99874bb-02b2-4c45-a3c8-a522b78e2026")
        UUID id,

        @Schema(example = "Alex Collin")
        String name,

        @Schema(example = "alex_collin@example.com")
        String email) {
}