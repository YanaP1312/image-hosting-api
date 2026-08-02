package com.image.hosting.services;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import com.image.hosting.models.helpers.ImageTags;
import com.image.hosting.repositories.ImageRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class LlmService {

    private static final String MODEL = "gemini-3.5-flash-lite";
    private static final int MAX_RETRIES = 2;
    private static final long RETRY_DELAY_MS = 8000L;
    private static final String PROMPT = """
            Analyze this image and return ONLY a valid JSON object (no markdown, no extra text) with exactly these three keys:
            
                        - "objects": array of distinct physical objects/things visible in the image (e.g. "tree", "car", "cloud")
                        - "tags": array of contextual descriptors that are NOT physical objects — setting, time of day, weather, mood, style, etc.
                        - "colors": array of up to 3 most prominent colors, chosen ONLY from this exact list: white, black, grey, yellow, red, blue, green, brown, pink, orange, purple
            
                        Example output:
                        {
                          "objects": ["palm tree", "sun", "sea", "beach", "boat", "sand"],
                          "tags": ["sunset", "beach scene", "silhouette", "tropical", "reflection on water"],
                          "colors": ["orange", "pink", "purple"]
                        }
            
                        Return only the JSON object, nothing else.
            """;

    private final Client geminiClient;
    private final ObjectMapper objectMapper;
    private final FileService fileService;
    private final ImageRepository imageRepository;

    @Async
    public void tagImageAsync(UUID imageId, String storageKey, String contentType) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                byte[] imageBytes = fileService.download(storageKey);

                long startTime = System.currentTimeMillis();

                GenerateContentResponse response = geminiClient.models.generateContent(
                        MODEL,
                        Content.fromParts(
                                Part.fromText(PROMPT),
                                Part.fromBytes(imageBytes, contentType)
                        ),
                        GenerateContentConfig.builder()
                                .responseMimeType("application/json")
                                .build()
                );

                String rawJson = cleanJson(response.text());
                ImageTags tags = objectMapper.readValue(rawJson, ImageTags.class);

                imageRepository.updateImageTags(imageId, tags);

                long duration = System.currentTimeMillis() - startTime;
                log.info("Successfully tagged image {} in {} ms", imageId, duration);
                return;

            } catch (Exception e) {
                log.error("Attempt: {}/{}. Failed to tag image {}: {}", attempt, MAX_RETRIES, imageId, e.getMessage());
                if (attempt < MAX_RETRIES) {
                    sleep();
                }
            }
        }
        log.error("All retry attempts failed for image {}", imageId);
        imageRepository.markTaggingFailed(imageId);
    }

    private String cleanJson(String text) {
        if (text == null) return null;

        return text.trim()
                .replaceAll("^```json", "")
                .replaceAll("^```", "")
                .replaceAll("```$", "")
                .trim();
    }

    private void sleep() {
        try {
            Thread.sleep(RETRY_DELAY_MS);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

}
