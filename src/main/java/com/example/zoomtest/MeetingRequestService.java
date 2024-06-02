package com.example.zoomtest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@EnableScheduling
public class MeetingRequestService {

  @Autowired
  private MeetingRequestRepository meetingRequestRepository;

  @Autowired
  private ZoomApiService zoomApiService;

  /**
   * Периодически проверяет запросы на совпадение и создает встречи для совпадающих запросов.
   * Выполняется с начальной задержкой в 5 секунд, затем каждые 5 секунд.
   */
  @Scheduled(initialDelay = 5000, fixedRate = 5000)
  public void checkRequests() throws Exception {
    System.out.println("Scheduled method checkRequests() is running...");
    List<MeetingRequest> waitingRequests = meetingRequestRepository.findByStatus("ожидание");

    for (int i = 0; i < waitingRequests.size(); i++) {
      for (int j = i + 1; j < waitingRequests.size(); j++) {
        MeetingRequest request1 = waitingRequests.get(i);
        MeetingRequest request2 = waitingRequests.get(j);

        boolean isMatching = isMatching(request1, request2);
        // Вывод в лог результатов проверки совпадения
        System.out.println("Matching requests: " + isMatching);

        if (isMatching(request1, request2)) {
          createMeetingForRequests(request1, request2);
          break;
        }
      }
    }
  }

  /**
   * Создает новый запрос на встречу с заданной темой, временем начала и длительностью.
   * Устанавливает статус "ожидание" и сохраняет запрос в репозиторий.
   *
   * @param topic Тема встречи
   * @param startTime Время начала встречи
   * @param duration Длительность встречи
   */
  public void createRequest(String topic, String startTime, int duration) {
    MeetingRequest request = new MeetingRequest();
    request.setTopic(topic);
    request.setStartTime(startTime);
    request.setDuration(duration);
    request.setStatus("ожидание");
    meetingRequestRepository.save(request);
  }

  /**
   * Проверяет, совпадают ли два запроса на встречу по теме, времени начала и длительности.
   * Если совпадают, выводит информацию о запросах в консоль.
   *
   * @param req1 Первый запрос на встречу
   * @param req2 Второй запрос на встречу
   * @return true, если запросы совпадают, иначе false
   */
  private boolean isMatching(MeetingRequest req1, MeetingRequest req2) {
    boolean match = req1.getTopic().equals(req2.getTopic()) &&
        req1.getStartTime().equals(req2.getStartTime()) &&
        req1.getDuration() == req2.getDuration();

    if (match) {
      System.out.println("Requests are matching:");
      System.out.println("Request 1: " + req1);
      System.out.println("Request 2: " + req2);
    }

    return match;
  }

  /**
   * Создает Zoom-встречу для двух совпадающих запросов на встречу.
   * Устанавливает одинаковые ссылки на встречу для обоих запросов и обновляет их статус на "сопоставлено".
   *
   * @param req1 Первый запрос на встречу
   * @param req2 Второй запрос на встречу
   * @throws Exception если не удалось создать встречу в Zoom
   */
  private void createMeetingForRequests(MeetingRequest req1, MeetingRequest req2) throws Exception {
    System.out.println("Creating meeting for requests:");
    System.out.println("Request 1: " + req1);
    System.out.println("Request 2: " + req2);

    List<MeetingRequest> existingRequests = meetingRequestRepository.findByTopicAndStartTimeAndDurationAndStatus(
        req1.getTopic(), req1.getStartTime(), req1.getDuration(), "сопоставлено");

    if (!existingRequests.isEmpty()) {
      req1.setStatus("сопоставлено");
      req2.setStatus("сопоставлено");
      meetingRequestRepository.save(req1);
      meetingRequestRepository.save(req2);
      return;
    }

    ResponseEntity<String> response1 = zoomApiService.createMeeting(req1.getTopic(), req1.getDuration(), req1.getStartTime());
    if (response1.getStatusCode().is2xxSuccessful()) {
      Map<String, Object> responseBody1 = new ObjectMapper().readValue(response1.getBody(), new TypeReference<>() {});
      String joinUrl1 = (String) responseBody1.get("join_url");
      String startUrl1 = (String) responseBody1.get("start_url");

      req1.setZoomJoinUrl(joinUrl1);
      req1.setZoomStartUrl(startUrl1);

      // Установка одинаковой ссылки для обоих запросов
      req2.setZoomJoinUrl(joinUrl1);
      req2.setZoomStartUrl(startUrl1);

      req1.setStatus("сопоставлено");
      req2.setStatus("сопоставлено");

      meetingRequestRepository.save(req1);
      meetingRequestRepository.save(req2);

    } else {
      throw new RuntimeException("Failed to create first Zoom meeting: " + response1.getStatusCode());
    }
  }

  /**
   * Возвращает список всех запросов на встречи.
   *
   * @return список всех запросов на встречи
   */
  public List<MeetingRequest> getAllRequests() {
    return meetingRequestRepository.findAll();
  }
}


