package com.example.zoomtest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meeting")
public class MeetingRequestController {

  @Autowired
  private MeetingRequestService meetingRequestService;

  @Autowired
  private ZoomApiService zoomApiService;

  /**
   * Создает заявку на встречу с заданными параметрами.
   * @param topic тема встречи
   * @param startTime время начала встречи в формате ISO 8601
   * @param duration длительность встречи в минутах
   * @return ответ с сообщением о создании заявки
   */
  @PostMapping("/create-request")
  public ResponseEntity<String> createMeetingRequest(@RequestParam String topic, @RequestParam String startTime, @RequestParam int duration) {
    try {
      meetingRequestService.createRequest(topic, startTime, duration);
      return ResponseEntity.ok("Заявка создана");
    } catch (Exception e) {
      return ResponseEntity.status(500).body(e.getMessage());
    }
  }

  /**
   * Создает встречу в Zoom с заданными параметрами.
   * @param topic тема встречи
   * @param duration длительность встречи в минутах
   * @param startTime время начала встречи в формате ISO 8601
   * @return ответ от API Zoom с деталями созданной встречи
   */
  @PostMapping("/create-meeting")
  public ResponseEntity<String> createMeeting(
      @RequestParam String topic,
      @RequestParam int duration,
      @RequestParam String startTime) { // добавлен параметр startTime
    try {
      System.out.println("Creating meeting with topic: " + topic + ", duration: " + duration + ", start time: " + startTime);
      return zoomApiService.createMeeting(topic, duration, startTime); // передаем startTime
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(500).body(e.getMessage());
    }
  }

  /**
   * Возвращает список всех заявок на встречи.
   * @return список всех заявок на встречи
   */
  @GetMapping("/requests")
  public ResponseEntity<List<MeetingRequest>> getAllRequests() {
    return ResponseEntity.ok(meetingRequestService.getAllRequests());
  }
}

