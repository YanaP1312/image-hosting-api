package com.image.hosting.services;

import com.image.hosting.dto.requests.auth.LoginRequest;
import com.image.hosting.dto.requests.auth.RegisterRequest;
import com.image.hosting.dto.responses.auth.LoginResponse;
import com.image.hosting.dto.responses.auth.RegisterResponse;
import com.image.hosting.exceptions.auth.EmailAlreadyTakenException;
import com.image.hosting.exceptions.auth.InvalidCredentialException;
import com.image.hosting.models.Session;
import com.image.hosting.models.User;
import com.image.hosting.repositories.SessionRepository;
import com.image.hosting.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class AuthService {

    private SessionRepository sessionRepository;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private TokenService tokenService;

    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.findUserByEmail(request.email()).isPresent()) {
            throw new EmailAlreadyTakenException("This email already exist");
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        User newUser = User.builder()
                .name(request.name())
                .email(request.email())
                .passwordHash(encodedPassword)
                .build();

        User created = userRepository.createUser(newUser);

        return new RegisterResponse(created.getId(), created.getName(), created.getEmail());
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findUserByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialException("Invalid email or password");
        }

        String rawToken = tokenService.generateToken();
        String hashedToken = tokenService.hashToken(rawToken);

        Session session = Session.builder()
                .id(hashedToken)
                .userId(user.getId())
                .expiresAt(LocalDateTime.now().plusHours(2))
                .build();

        sessionRepository.createSession(session);

        return new LoginResponse(rawToken);
    }

    public void logout(String rawToken) {

        String hashedToken = tokenService.hashToken(rawToken);
        sessionRepository.deleteSessionById(hashedToken);
    }

}
