package com.image.hosting.services;

import com.image.hosting.dto.responses.image.GetImageListResponse;
import com.image.hosting.dto.responses.image.GetImageMetadataResponse;
import com.image.hosting.dto.responses.image.GetImageResponse;
import com.image.hosting.dto.responses.image.PostImageResponse;
import com.image.hosting.exceptions.UserNotFoundException;
import com.image.hosting.exceptions.image.ForbiddenImageAccessException;
import com.image.hosting.exceptions.image.ImageNotFoundException;
import com.image.hosting.exceptions.image.ImageTooLargeException;
import com.image.hosting.exceptions.image.UnsupportedImageFormatException;
import com.image.hosting.models.Image;
import com.image.hosting.models.User;
import com.image.hosting.models.helpers.ImageContent;
import com.image.hosting.repositories.ImageRepository;
import com.image.hosting.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ImageService {

  private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
  private static final List<String> ALLOWED_CONTENT_TYPES = List.of("image/jpeg", "image/png");
  private final ImageRepository imageRepository;
  private final UserRepository userRepository;
  private final FileService fileService;
  private final LlmService llmService;


  public PostImageResponse uploadImage(MultipartFile file, UUID userId) {

    if (file.getSize() > MAX_FILE_SIZE) {
      throw new ImageTooLargeException("Image must not exceed 10MB");
    }

    String contentType = file.getContentType();
    if(contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)){
      throw new UnsupportedImageFormatException("Only JPEG and PNG images are supported");
    }

    UUID imageId = UUID.randomUUID();
    String storageKey = "users/" + userId + "/" + imageId;

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

    llmService.tagImageAsync(created.getId(), created.getStorageKey(), created.getContentType());

    return new PostImageResponse(created.getId(), created.getCreatedAt(), created.getContentType(), created.getTags(), created.getTaggingStatus());

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

  public GetImageMetadataResponse getImageMetadataById(UUID imageId) {
    Image image = imageRepository.findImageById(imageId).orElseThrow(() ->
        new ImageNotFoundException("Image not found"));

    User user = userRepository.findUserById(image.getUserId()).orElseThrow(() ->
        new UserNotFoundException("User not found"));

    return new GetImageMetadataResponse(
        image.getId(),
        image.getUserId(),
        user.getName(),
        image.getCreatedAt(),
        image.getContentType(),
        image.getTags(),
        image.getTaggingStatus()
    );
  }

  public GetImageListResponse getImages(String query, int page, int pageSize) {
    List<Image> images = (query != null && !query.isBlank())
        ? imageRepository.searchImages(page, pageSize, query)
        : imageRepository.findAllImages(page, pageSize);

    int totalCount = (query != null && !query.isBlank())
        ? imageRepository.countSearchResults(query)
        : imageRepository.countImage();

    return buildListResponse(images, page, pageSize, totalCount);
  }

  public GetImageListResponse getImageByUserId(UUID userId, String query, int page, int pageSize) {
    List<Image> images = (query != null && !query.isBlank())
        ? imageRepository.searchImagesByUserId(userId, query, page, pageSize)
        : imageRepository.findImagesByUserId(userId, page, pageSize);

    int totalCount = (query != null && !query.isBlank())
        ? imageRepository.countSearchResultsByUserId(userId, query)
        : imageRepository.countImagesByUserId(userId);

    return buildListResponse(images, page, pageSize, totalCount);
  }


  private GetImageResponse toResponse(Image image) {
    return new GetImageResponse(image.getId(), image.getCreatedAt(), image.getContentType(), image.getTags());
  }

  private GetImageListResponse buildListResponse(List<Image> images, int page, int pageSize, int totalCount) {
    int totalPages = (int) Math.ceil((double) totalCount / pageSize);

    List<GetImageResponse> imageResponses = images.stream()
        .map(this::toResponse)
        .toList();
    return new GetImageListResponse(imageResponses, page, pageSize, totalCount, totalPages);
  }

  public void deleteImage(UUID imageId, UUID currentUser) {
    Image image = imageRepository.findImageById(imageId).orElseThrow(() ->
        new ImageNotFoundException("Image not found"));

    if (!image.getUserId().equals(currentUser)) {
      throw new ForbiddenImageAccessException("You can only delete your own images");
    }

    imageRepository.deleteImageById(imageId);
    fileService.delete(image.getStorageKey());


  }

}
