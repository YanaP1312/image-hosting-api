package com.image.hosting.dto.responses.image;

import com.image.hosting.models.helpers.ImageTags;
import com.image.hosting.models.helpers.TaggingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record GetImageMetadataResponse(
        UUID id,
        UUID userId,
        String userName,
        LocalDateTime createdAt,
        String contentType,
        ImageTags tags,
        TaggingStatus taggingStatus
) {
}
