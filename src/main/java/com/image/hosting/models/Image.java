package com.image.hosting.models;

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
    private ImageTags tags;
}
