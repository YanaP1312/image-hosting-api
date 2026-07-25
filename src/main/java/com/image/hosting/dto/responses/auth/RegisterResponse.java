package com.image.hosting.dto.responses.auth;

import java.util.UUID;

public record RegisterResponse (UUID id, String name, String email){
}
