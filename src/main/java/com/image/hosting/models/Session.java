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
public class Session {
  private String id;
  private UUID userId;
  private LocalDateTime createdAt;
  private LocalDateTime expiresAt;
}
