package com.image.hosting.controllers;

import com.image.hosting.dto.responses.user.GetCurrentUserResponse;
import com.image.hosting.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get the current authenticated user's data")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Returns the current user's profile"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired session"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/me")
    public GetCurrentUserResponse getCurrentUser(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        return userService.getCurrentUser(userId);
    }

    @Operation(summary = "Delete the current user's account (cascades to sessions and their images)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Account successfully deleted"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired session")
    })
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCurrentUser(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        userService.deleteUser(userId);
    }
}
