package com.image.hosting.dto.requests.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @Schema(example = "Alex Collin")
    @NotBlank(message = "Name is required")
    @Size(min = 3, message = "Name must be at least 3 characters long")
    String name,

    @Schema(example = "alex_collin@example.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email format")
    String email,

    @Schema(example = "securePass123")
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    String password
) {
}
