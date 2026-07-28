package com.image.hosting.repositories;


import com.image.hosting.models.Image;
import com.image.hosting.models.ImageTags;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class ImageRepository {

    private final ObjectMapper objectMapper;
    private final JdbcClient jdbcClient;


    private Image mapRow(ResultSet rs, int rowNum) throws SQLException {
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
                .contentType(rs.getString("content_type"))
                .tags(tags)
                .build();
    }

    ;

    public Image createImage(Image image) {
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
                        INSERT INTO images (id, user_id, storage_key, content_type, tags)
                        VALUES (:id, :user_id, :storage_key, :content_type, :tags::jsonb)
                        RETURNING id, user_id, storage_key, created_at, content_type, tags
                        """)
                .param("id", image.getId())
                .param("user_id", image.getUserId())
                .param("storage_key", image.getStorageKey())
                .param("content_type", image.getContentType())
                .param("tags", tagsJson)
                .query(this::mapRow)
                .single();
    }

    public Optional<Image> findImageById(UUID imageId) {
        return jdbcClient
                .sql("""
                        SELECT id, user_id, storage_key, created_at, content_type, tags
                        FROM images WHERE id = :id
                        """)
                .param("id", imageId)
                .query(this::mapRow)
                .optional();
    }

    public List<Image> findAllImages(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return jdbcClient
                .sql("""
                        SELECT id, user_id, storage_key, created_at, content_type, tags
                        FROM images
                        ORDER BY created_at DESC
                        LIMIT :pageSize OFFSET :offset
                        """)
                .param("pageSize", pageSize)
                .param("offset", offset)
                .query(this::mapRow)
                .list();
    }

    public List<Image> searchImages(int page, int pageSize, String query) {
        int offset = (page - 1) * pageSize;

        return jdbcClient
                .sql("""
                        SELECT id, user_id, storage_key, created_at, content_type, tags
                        FROM images
                        WHERE tags::text ILIKE :query
                        ORDER by created_at DESC
                        LIMIT :pageSize OFFSET :offset
                        """)
                .param("pageSize", pageSize)
                .param("offset", offset)
                .param("query", "%" + query + "%")
                .query(this::mapRow)
                .list();
    }

    public List<Image> findImagesByUserId(UUID userId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return jdbcClient
                .sql("""
                        SELECT id, user_id, storage_key, created_at, content_type, tags
                        FROM images
                        WHERE user_id = :userId
                        ORDER BY created_at DESC
                        LIMIT :pageSize OFFSET :offset
                        """)
                .param("userId", userId)
                .param("pageSize", pageSize)
                .param("offset", offset)
                .query(this::mapRow)
                .list();
    }

    public List<Image> searchImagesByUserId(UUID userId, String query, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return jdbcClient
                .sql("""
                        SELECT id, user_id, storage_key, created_at, content_type, tags
                        FROM images
                        WHERE user_id = :userId AND tags::text ILIKE :query
                        ORDER BY created_at DESC
                        LIMIT :pageSize OFFSET :offset
                        """)
                .param("userId", userId)
                .param("query", "%" + query + "%")
                .param("pageSize", pageSize)
                .param("offset", offset)
                .query(this::mapRow)
                .list();
    }


    public void deleteImageById(UUID id) {
        jdbcClient
                .sql("DELETE FROM images WHERE id = :id")
                .param("id", id)
                .update();
    }

    public Image updateImageTags(UUID id, ImageTags tags) {
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
                        RETURNING id, user_id, storage_key, created_at, content_type, tags
                        """)
                .param("tags", tagsJson)
                .param("id", id)
                .query(this::mapRow)
                .single();
    }

    public int countImage() {
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

    public int countImagesByUserId(UUID userId) {
        return jdbcClient
                .sql("SELECT COUNT(*) FROM images WHERE user_id = :userId")
                .param("userId", userId)
                .query(Integer.class)
                .single();
    }

    public int countSearchResultsByUserId(UUID userId, String query) {
        return jdbcClient
                .sql("""
                        SELECT COUNT(*) FROM images
                        WHERE user_id = :userId AND tags::text ILIKE :query
                        """)
                .param("userId", userId)
                .param("query", "%" + query + "%")
                .query(Integer.class)
                .single();
    }
}

