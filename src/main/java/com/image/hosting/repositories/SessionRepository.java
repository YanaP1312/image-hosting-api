package com.image.hosting.repositories;

import com.image.hosting.models.Session;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@AllArgsConstructor
public class SessionRepository {

    private final JdbcClient jdbcClient;

    public void createSession(Session session) {
        jdbcClient
                .sql("""
                        INSERT INTO sessions (id, user_id, expires_at)
                        VALUES (:id, :userId, :expiresAt)
                        RETURNING id, user_id, created_at, expires_at
                        """)
                .param("id", session.getId())
                .param("userId", session.getUserId())
                .param("expiresAt", session.getExpiresAt())
                .query(Session.class)
                .single();
    }

    public Optional<Session> findSessionById(String token) {
        return jdbcClient
                .sql("""
                        SELECT id, user_id, created_at, expires_at
                        FROM sessions WHERE id = :token
                        """)
                .param("token", token)
                .query(Session.class)
                .optional();
    }

    public void deleteSessionById(String token) {
        jdbcClient
                .sql("DELETE FROM sessions WHERE id = :token")
                .param("token", token)
                .update();
    }
}
