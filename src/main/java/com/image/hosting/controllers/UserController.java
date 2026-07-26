package com.image.hosting.controllers;

import com.image.hosting.dto.responses.user.GetCurrentUserResponse;
import com.image.hosting.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    public final UserService userService;

    @GetMapping("/me")
    public GetCurrentUserResponse getCurrentUser(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        return userService.getCurrentUser(userId);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCurrentUser(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        userService.deleteUser(userId);
    }
}
