package com.image.hosting.controllers;

import com.image.hosting.dto.requests.auth.LoginRequest;
import com.image.hosting.dto.requests.auth.RegisterRequest;
import com.image.hosting.dto.responses.auth.LoginResponse;
import com.image.hosting.dto.responses.auth.RegisterResponse;
import com.image.hosting.services.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @DeleteMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestHeader("Authorization") String authHeader){
        String rawToken = authHeader.substring(7);
        authService.logout(rawToken);
    }

}
