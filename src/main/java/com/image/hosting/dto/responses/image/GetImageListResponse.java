package com.image.hosting.dto.responses.image;

import java.util.List;

public record GetImageListResponse(
        List<GetImageResponse> images,
        int page,
        int pageSize,
        int totalCount,
        int totalPages
) {}
