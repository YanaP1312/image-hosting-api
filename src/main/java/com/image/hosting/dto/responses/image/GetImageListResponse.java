package com.image.hosting.dto.responses.image;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record GetImageListResponse(
        List<GetImageResponse> images,

        @Schema(example = "1")
        int page,

        @Schema(example = "50")
        int pageSize,

        @Schema(example = "150")
        int totalCount,

        @Schema(example = "3")
        int totalPages
) {
}
