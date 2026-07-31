package com.image.hosting.controllers;

import com.image.hosting.dto.requests.auth.LoginRequest;
import com.image.hosting.dto.requests.auth.RegisterRequest;
import com.image.hosting.dto.responses.auth.LoginResponse;
import com.image.hosting.dto.responses.auth.RegisterResponse;
import com.image.hosting.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new user", security = {})
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", description = "Validation failed (invalid email, blank fields, password too short)"),
            @ApiResponse(responseCode = "409", description = "Email is already registered")
    })
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @Operation(summary = "Log in with email and password", security = {})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully logged in, returns session token"),
            @ApiResponse(responseCode = "400", description = "Validation failed (invalid email format, blank fields)"),
            @ApiResponse(responseCode = "401", description = "Invalid email or password")
    })
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @Operation(summary = "Log out and invalidate the current session")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successfully logged out"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired session")
    })
    @DeleteMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestHeader("Authorization") String authHeader) {
        String rawToken = authHeader.substring(7);
        authService.logout(rawToken);
    }

}
