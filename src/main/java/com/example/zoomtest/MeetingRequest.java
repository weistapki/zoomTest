package com.example.zoomtest;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "meeting_request")
public class MeetingRequest {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String topic;
  private String startTime;
  private int duration;
  private String status; // например, "ожидание", "сопоставлено", "завершено"

  @Column(length = 1000) // Увеличиваем длину столбца до 1000 символов
  private String zoomJoinUrl; // Ссылка для присоединения к встрече в Zoom

  @Column(length = 1000) // Увеличиваем длину столбца до 1000 символов
  private String zoomStartUrl; // Ссылка для начала встречи в Zoom (для хоста)


}
