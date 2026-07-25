package com.image.hosting.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class User {
   private UUID id;
    private String name;
    private String email;
    private String passwordHash;
    private LocalDateTime createdAt;
}
