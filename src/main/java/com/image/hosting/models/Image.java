package com.image.hosting.models;

import com.image.hosting.models.helpers.ImageTags;
import com.image.hosting.models.helpers.TaggingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class Image {
    private UUID id;
    private UUID userId;
    private String storageKey;
    private LocalDateTime createdAt;
    private String contentType;
    private ImageTags tags;
    private TaggingStatus taggingStatus;

}
