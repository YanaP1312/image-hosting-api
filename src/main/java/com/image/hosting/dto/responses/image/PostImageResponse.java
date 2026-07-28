package com.image.hosting.dto.responses.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.image.hosting.models.ImageTags;

import java.time.LocalDateTime;
import java.util.UUID;

public record PostImageResponse (
        UUID id,

        @JsonProperty("created_at")
        LocalDateTime createdAt,

        @JsonProperty("content_type")
        String contentType,

        ImageTags tags
){
}
