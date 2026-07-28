package com.image.hosting.controllers;

import com.image.hosting.dto.responses.image.PostImageResponse;
import com.image.hosting.services.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/images")
@AllArgsConstructor

public class ImageController {

    private final ImageService imageService;

    @PostMapping
    public PostImageResponse uploadImage(@RequestParam("file") MultipartFile file, Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();

        return imageService.uploadImage(file, userId);
    }
}
