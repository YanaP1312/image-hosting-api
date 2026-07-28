package com.image.hosting.controllers;

import com.image.hosting.dto.responses.image.PostImageResponse;
import com.image.hosting.models.ImageContent;
import com.image.hosting.services.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/images")
@AllArgsConstructor

public class ImageController {

    private final ImageService imageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostImageResponse uploadImage(@RequestParam("file") MultipartFile file, Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();

        return imageService.uploadImage(file, userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImageContent(@PathVariable UUID id){
        ImageContent content = imageService.getImageById(id);
return ResponseEntity.ok().contentType(MediaType.parseMediaType(content.contentType()))
        .body(content.bytes());
    }

}
