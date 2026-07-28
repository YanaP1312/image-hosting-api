package com.image.hosting.repositories;

import com.image.hosting.models.Image;
import com.image.hosting.models.ImageTags;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class ImageRepository {

    private final ObjectMapper objectMapper;
    private final JdbcClient jdbcClient;


    private final RowMapper<Image> imageRowMapper = (rs, rowNum) -> {
        ImageTags tags = null;
        String tagsJson = rs.getString("tags");
        if (tagsJson != null) {
            try {
                tags = objectMapper.readValue(tagsJson, ImageTags.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return Image.builder()
                .id(UUID.fromString(rs.getString("id")))
                .userId(UUID.fromString(rs.getString("user_id")))
                .storageKey(rs.getString("storage_key"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .tags(tags)
                .build();
    };

    public Image createImage(Image image){
        String tagsJson = null;
        if (image.getTags() != null) {
            try {
                tagsJson = objectMapper.writeValueAsString(image.getTags());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return jdbcClient
                .sql("""
                        INSERT INTO images (id, user_id, storage_key, tags)
                        VALUES (:id, :user_id, :storage_key, :tags::jsonb)
                        RETURNING id, user_id, storage_key, created_at, tags
                        """)
                .param("id", image.getId())
                .param("user_id", image.getUserId())
                .param("storage_key", image.getStorageKey())
                .param("tags", tagsJson)
                .query(imageRowMapper)
                .single();
    }

    public List<Image> findAllImages(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return jdbcClient
                .sql("""
                    SELECT id, user_id, storage_key, tags, created_at
                    FROM images
                    ORDER BY created_at DESC
                    LIMIT :pageSize OFFSET :offset
                    """)
                .param("pageSize", pageSize)
                .param("offset", offset)
                .query(imageRowMapper)
                .list();
    }

    public Optional<Image> findImageById(UUID imageId){
        return jdbcClient
                .sql("""
                        SELECT id, user_id, storage_key, created_at, tags
                        FROM images WHERE id = :id
                        """)
                .param("id", imageId)
                .query(imageRowMapper)
                .optional();
    }

    public List<Image> findImagesByUserId(UUID userId) {
        return jdbcClient
                .sql("""
                    SELECT id, user_id, storage_key, tags, created_at
                    FROM images
                    WHERE user_id = :userId
                    ORDER BY created_at DESC
                    """)
                .param("userId", userId)
                .query(imageRowMapper)
                .list();
    }

    public List<Image> searchImages(int page, int pageSize, String query){
        int offset = (page - 1) * pageSize;

        return jdbcClient
                .sql("""
                        SELECT id, user_id, storage_key, tags, created_at
                        FROM images
                        WHERE tags::text ILIKE :query
                        ORDER by created_at DESC
                        LIMIT :pageSize OFFSET :offset
                        """)
                .param("pageSize", pageSize)
                .param("offset", offset)
                .param("query", "%" + query + "%")
                .query(imageRowMapper)
                .list();
    }

    public void deleteImageById(UUID id){
        jdbcClient
                .sql("DELETE FROM images WHERE id = :id")
                .param("id", id)
                .update();
    }

    public Image updateImageTags(UUID id, ImageTags tags){
        String tagsJson;
        try {
            tagsJson = objectMapper.writeValueAsString(tags);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return jdbcClient
                .sql("""
                        UPDATE images
                        SET tags = :tags::jsonb
                        WHERE id = :id
                        RETURNING id, user_id, storage_key, created_at, tags
                        """)
                .param("tags", tagsJson)
                .param("id", id)
                .query(imageRowMapper)
                .single();
    }

    public int countImage(){
        return jdbcClient
                .sql("SELECT COUNT(*) FROM images")
                .query(Integer.class)
                .single();
    }

    public int countSearchResults(String query) {
        return jdbcClient
                .sql("""
                    SELECT COUNT(*) FROM images
                    WHERE tags::text ILIKE :query
                    """)
                .param("query", "%" + query + "%")
                .query(Integer.class)
                .single();
    }
}

