package com.image.hosting.dto.responses.image;

import com.image.hosting.models.helpers.ImageTags;
import com.image.hosting.models.helpers.TaggingStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record GetImageMetadataResponse(
        @Schema(example = "87e2042b-1967-4a6f-9f31-06da139ea435")
        UUID id,

        @Schema(example = "b99874bb-02b2-4c45-a3c8-a522b78e2026")
        UUID userId,

        @Schema(example = "Alex Collin")
        String userName,

        @Schema(example = "2026-07-28 21:25:13.374 +0200")
        LocalDateTime createdAt,

        @Schema(example = "image/jpeg", description = "MIME type of the uploaded image, e.g. image/jpeg or image/png")
        String contentType,

        ImageTags tags,

        @Schema(allowableValues = {"PENDING", "COMPLETED", "FAILED"}, example = "COMPLETED")
        TaggingStatus taggingStatus
) {
}
