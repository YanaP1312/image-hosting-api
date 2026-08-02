package com.image.hosting.controllers;

import com.image.hosting.dto.responses.image.GetImageListResponse;
import com.image.hosting.dto.responses.image.GetImageMetadataResponse;
import com.image.hosting.dto.responses.image.PostImageResponse;
import com.image.hosting.models.helpers.ImageContent;
import com.image.hosting.services.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

  @Operation(summary = "Upload a new image (max 10MB)")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Image successfully uploaded; AI tagging happens asynchronously"),
      @ApiResponse(responseCode = "400", description = "Unsupported type of image",
          content = @Content(examples = @ExampleObject(value = "{\"error\": \"Only JPEG and PNG images are supported\"}"))),
      @ApiResponse(responseCode = "401", description = "Invalid or expired session",
          content = @Content(examples = @ExampleObject(value = "{\"error\": \"Invalid or expired session\"}"))),
      @ApiResponse(responseCode = "413", description = "Image exceeds the 10MB size limit",
          content = @Content(examples = @ExampleObject(value = "{\"error\": \"Image must not exceed 10MB\"}")))
  })
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public PostImageResponse uploadImage(
      @Parameter(description = "Image file to upload", required = true)
      @RequestParam("file") MultipartFile file,
      Authentication authentication) {
    UUID userId = (UUID) authentication.getPrincipal();

    return imageService.uploadImage(file, userId);
  }

  @Operation(summary = "Get the raw image bytes by ID (direct URL, renders in browser)", security = {})
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Returns raw image bytes with correct Content-Type"),
      @ApiResponse(responseCode = "404", description = "Image not found",
          content = @Content(examples = @ExampleObject(value = "{\"error\": \"Image not found\"}")))
  })
  @GetMapping("/{id}")
  public ResponseEntity<byte[]> getImageContent(@PathVariable UUID id) {
    ImageContent content = imageService.getImageById(id);

    return ResponseEntity.ok().contentType(MediaType.parseMediaType(content.contentType()))
        .body(content.bytes());
  }

  @Operation(summary = "Get image metadata including tags and uploader's name", security = {})
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Returns image metadata"),
      @ApiResponse(responseCode = "404", description = "Image not found",
          content = @Content(examples = @ExampleObject(value = "{\"error\": \"Image not found\"}")))
  })
  @GetMapping("/{id}/metadata")
  public GetImageMetadataResponse getImageMetadataById(@PathVariable UUID id) {
    return imageService.getImageMetadataById(id);
  }

  @Operation(summary = "List or search public images (paginated, 50 per page by default)", security = {})
  @ApiResponse(responseCode = "200", description = "Returns paginated list of images")
  @GetMapping
  public GetImageListResponse getImages(
      @Parameter(description = "Free-text search query (searches objects, tags, and colors)")
      @RequestParam(required = false) String query,
      @Parameter(description = "Page number, starting from 1")
      @RequestParam(defaultValue = "1") int page,
      @Parameter(description = "Number of images per page")
      @RequestParam(defaultValue = "50") int pageSize
  ) {
    return imageService.getImages(query, page, pageSize);
  }

  @Operation(summary = "List or search the current user's own images (paginated, 50 per page by default)")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Returns paginated list of the user's images"),
      @ApiResponse(responseCode = "401", description = "Invalid or expired session",
          content = @Content(examples = @ExampleObject(value = "{\"error\": \"Invalid or expired session\"}")))
  })
  @GetMapping("/my")
  public GetImageListResponse getMyImages(
      @Parameter(description = "Free-text search query (searches objects, tags, and colors)")
      @RequestParam(required = false) String query,
      @Parameter(description = "Page number, starting from 1")
      @RequestParam(defaultValue = "1") int page,
      @Parameter(description = "Number of images per page")
      @RequestParam(defaultValue = "50") int pageSize,
      Authentication authentication
  ) {
    UUID userId = (UUID) authentication.getPrincipal();
    return imageService.getImageByUserId(userId, query, page, pageSize);

  }

  @Operation(summary = "Delete your own image")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Image successfully deleted"),
      @ApiResponse(responseCode = "401", description = "Invalid or expired session",
          content = @Content(examples = @ExampleObject(value = "{\"error\": \"Invalid or expired session\"}"))),
      @ApiResponse(responseCode = "403", description = "You can only delete your own images",
          content = @Content(examples = @ExampleObject(value = "{\"error\": \"You can only delete your own images\"}"))),
      @ApiResponse(responseCode = "404", description = "Image not found",
          content = @Content(examples = @ExampleObject(value = "{\"error\": \"Image not found\"}")))
  })
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteImageById(@PathVariable UUID id, Authentication authentication) {
    UUID currentUser = (UUID) authentication.getPrincipal();
    imageService.deleteImage(id, currentUser);
  }

}
