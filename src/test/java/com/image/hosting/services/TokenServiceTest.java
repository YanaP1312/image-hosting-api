package com.image.hosting.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TokenServiceTest {

  private final TokenService tokenService = new TokenService();

  @Test
  void hashToken_producesSameHashForSameInput() {
    String token = "test-token";
    assertEquals(tokenService.hashToken(token), tokenService.hashToken(token));
  }

  @Test
  void hashToken_producesDifferentHashForDifferentInput() {
    assertNotEquals(tokenService.hashToken("token1"), tokenService.hashToken("token2"));
  }

  @Test
  void generateToken_producesUniqueTokens() {
    assertNotEquals(tokenService.generateToken(), tokenService.generateToken());
  }
}
