package com.image.hosting.repositories;

import com.image.hosting.models.User;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class UserRepository {

    private final JdbcClient jdbcClient;

    public User createUser(User user){
        return jdbcClient
                .sql("""
                INSERT INTO users (name, email, password_hash) 
                VALUES (:name, :email, :password_hash)
                RETURNING id, name, email, password_hash, created_at
                """)
                .param("name", user.getName())
                .param("email", user.getEmail())
                .param("password_hash", user.getPasswordHash())
                .query(User.class)
                .single();
    }

    public Optional<User> findById(UUID id){
        return jdbcClient
                .sql("""
                        SELECT id, name, email, password_hash, created_at
                        FROM users WHERE id = :id
                        """)
                .param("id", id)
                .query(User.class)
                .optional();
    }

    public Optional<User> findByEmail(String email){
        return jdbcClient
                .sql("""
                SELECT id, name, email, password_hash, created_at
                FROM users WHERE email = :email
                """)
                .param("email", email)
                .query(User.class)
                .optional();
    }

    public void deleteUserById(UUID id){
        jdbcClient
                .sql("DELETE FROM users WHERE id = :id")
                .param("id", id)
                .update();
    }
}
