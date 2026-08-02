package com.image.hosting.dto.responses.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.image.hosting.models.helpers.ImageTags;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record GetImageResponse(
    @Schema(example = "87e2042b-1967-4a6f-9f31-06da139ea435")
    UUID id,

    @Schema(example = "2026-07-28 21:25:13.374 +0200")
    @JsonProperty("created_at")
    LocalDateTime createdAt,

    @Schema(example = "image/jpeg", description = "MIME type of the uploaded image, e.g. image/jpeg or image/png")
    @JsonProperty("content_type")
    String contentType,

    ImageTags tags
) {
}
