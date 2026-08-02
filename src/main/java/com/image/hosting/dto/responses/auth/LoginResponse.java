package com.image.hosting.dto.responses.auth;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
    @Schema(example = "LkFmpHJfuPqLEoVR3MZak0jsRODG8GQ-GratcZDkj09")
    String token
) {
}
