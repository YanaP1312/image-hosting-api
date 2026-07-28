package com.image.hosting.services;

import com.image.hosting.dto.responses.image.PostImageResponse;
import com.image.hosting.exceptions.image.ImageNotFoundException;
import com.image.hosting.exceptions.image.ImageTooLargeException;
import com.image.hosting.models.Image;
import com.image.hosting.models.ImageContent;
import com.image.hosting.repositories.ImageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ImageService {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private final ImageRepository imageRepository;
    private final FileService fileService;


    public PostImageResponse uploadImage(MultipartFile file, UUID userId) {

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ImageTooLargeException("Image must not exceed 10MB");
        }

        UUID imageId = UUID.randomUUID();
        String storageKey = "users/" + userId + "/" + imageId;
        String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";

        try {
            fileService.upload(file, storageKey);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image to storage", e);
        }

        Image image = Image.builder()
                .id(imageId)
                .userId(userId)
                .storageKey(storageKey)
                .contentType(contentType)
                .tags(null)
                .build();

        Image created = imageRepository.createImage(image);

        return new PostImageResponse(created.getId(), created.getCreatedAt(), created.getContentType(), created.getTags());

    }

    public ImageContent getImageById(UUID imageId) {

        Image image = imageRepository.findImageById(imageId).orElseThrow(() ->
                new ImageNotFoundException("Image not found"));

        try {
            byte[] bytes = fileService.download(image.getStorageKey());
            return new ImageContent(bytes, image.getContentType());
        } catch (IOException e) {
            throw new RuntimeException("Failed to download image from storage", e);
        }
    }
}
