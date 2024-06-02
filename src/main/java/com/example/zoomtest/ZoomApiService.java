package com.example.zoomtest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
@Service
public class ZoomApiService {

  @Autowired
  private ZoomAuthenticationHelper zoomAuthenticationHelper;

  @Autowired
  private RestTemplate restTemplate;

  @Value("${zoom.oauth2.api-url}")
  private String zoomApiUrl;

  private static final String CREATE_MEETING_URL = "%s/users/me/meetings";

  /**
   * Создает встречу в Zoom с заданными параметрами.
   * @param topic тема встречи
   * @param duration длительность встречи в минутах
   * @param startTime время начала встречи в формате ISO 8601
   * @return ответ от API Zoom с деталями созданной встречи
   * @throws Exception если не удается создать встречу
   */
  public ResponseEntity<String> createMeeting(String topic, int duration, String startTime) throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
    headers.setBearerAuth(zoomAuthenticationHelper.getAuthenticationToken());

    Map<String, Object> request = new HashMap<>();
    request.put("topic", topic);
    request.put("type", 2); // Scheduled meeting
    request.put("duration", duration);
    request.put("start_time", startTime); // добавляем startTime

    Map<String, Object> settings = new HashMap<>();
    settings.put("host_video", true);
    settings.put("participant_video", true);
    settings.put("join_before_host", true);
    settings.put("waiting_room", false);
    settings.put("mute_upon_entry", false);
    settings.put("watermark", false);
    settings.put("use_pmi", false);
    settings.put("approval_type", 2);
    settings.put("audio", "voip");
    settings.put("auto_recording", "local"); // Автоматическая запись в облако
    settings.put("meeting_authentication", false);
    settings.put("encryption_type", "enhanced_encryption");
    settings.put("end_meeting_when_no_participants", true); // Завершить встречу после ухода последнего участника

    request.put("settings", settings);

    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
    String url = String.format(CREATE_MEETING_URL, zoomApiUrl);

    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

    if (response.getStatusCode().is2xxSuccessful()) {
      return response;
    } else {
      throw new RuntimeException("Failed to create Zoom meeting: " + response.getStatusCode());
    }
  }

  /**
   * Получает токен аутентификации для доступа к API Zoom.
   * @return токен аутентификации
   * @throws Exception если не удается получить токен
   */
  public String getAuthToken() throws Exception {
    return zoomAuthenticationHelper.getAuthenticationToken();
  }
}


