package com.example.zoomtest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingRequestRepository extends JpaRepository<MeetingRequest, Long> {
  List<MeetingRequest> findByStatus(String status);
  List<MeetingRequest> findByTopicAndStartTimeAndDurationAndStatus(String topic, String startTime, int duration, String status);
}
