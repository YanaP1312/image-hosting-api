package com.image.hosting.services;

import com.image.hosting.dto.requests.auth.RegisterRequest;
import com.image.hosting.dto.responses.auth.RegisterResponse;
import com.image.hosting.exceptions.EmailAlreadyTakenException;
import com.image.hosting.models.User;
import com.image.hosting.repositories.SessionRepository;
import com.image.hosting.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private SessionRepository sessionRepository;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

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


}
