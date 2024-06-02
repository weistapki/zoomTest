package com.example.zoomtest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ZoomAuthResponse {
  // Поле accessToken содержит токен аутентификации
  @JsonProperty("access_token")
  private String accessToken;

  // Поле tokenType содержит тип токена
  @JsonProperty("token_type")
  private String tokenType;

  // Поле expiresIn содержит время жизни токена в секундах
  @JsonProperty("expires_in")
  private int expiresIn;

  private String scope; // Добавленное поле

}
