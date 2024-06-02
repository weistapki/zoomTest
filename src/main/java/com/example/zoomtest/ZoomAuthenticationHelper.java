package com.example.zoomtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Base64;

@Component
public class ZoomAuthenticationHelper {

  // Значения, загружаемые из внешнего источника конфигурации
  @Value("${zoom.oauth2.client-id}")
  private String zoomClientId;

  @Value("${zoom.oauth2.client-secret}")
  private String zoomClientSecret;

  @Value("${zoom.oauth2.issuer}")
  private String zoomIssuerUrl;

  @Value("${zoom.oauth2.account-id}")
  private String zoomAccountId;

  @Autowired
  private RestTemplate restTemplate;

  private String authToken;

  /**
   * Метод для получения текущего токена аутентификации Zoom.
   * Если токен еще не был получен, выполняет запрос для его получения.
   * @return текущий токен аутентификации
   * @throws Exception в случае ошибки получения токена
   */
  public synchronized String getAuthenticationToken() throws Exception {
    // Если токен еще не был получен или пуст, выполняем запрос для его получения
    if (this.authToken == null || this.authToken.isEmpty()) {
      fetchToken();
    }
    return this.authToken;
  }

  /**
   * Метод для выполнения запроса на получение токена аутентификации.
   * Выполняет запрос к серверу аутентификации Zoom для получения нового токена.
   * @throws Exception в случае ошибки выполнения запроса
   */
  private void fetchToken() throws Exception {
    String credentials = zoomClientId + ":" + zoomClientSecret;
    String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_FORM_URLENCODED));
    headers.add("Authorization", "Basic " + encodedCredentials);

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("grant_type", "account_credentials");
    map.add("account_id", zoomAccountId);

    HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
    String url = zoomIssuerUrl + "/token";

    System.out.println("Request URL: " + url);
    System.out.println("Headers: " + headers);
    System.out.println("Body: " + map);

    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

    System.out.println("Response: " + response.getBody());

    if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
      ObjectMapper mapper = new ObjectMapper();
      ZoomAuthResponse authResponse = mapper.readValue(response.getBody(), ZoomAuthResponse.class);
      this.authToken = authResponse.getAccessToken();
      System.out.println("Access Token: " + this.authToken);
    } else {
      throw new Exception("Failed to fetch access token");
    }
  }
}
