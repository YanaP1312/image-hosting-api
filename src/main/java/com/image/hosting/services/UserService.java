package com.image.hosting.services;

import com.image.hosting.dto.responses.user.GetCurrentUserResponse;
import com.image.hosting.exceptions.UserNotFoundException;
import com.image.hosting.models.User;
import com.image.hosting.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public GetCurrentUserResponse getCurrentUser(UUID userId) {
    User user = userRepository.findUserById(userId)
        .orElseThrow(() -> new UserNotFoundException("User not found"));

    return new GetCurrentUserResponse(user.getId(), user.getName(), user.getEmail(), user.getCreatedAt());
  }

  public void deleteUser(UUID userId) {
    userRepository.deleteUserById(userId);
  }
}
