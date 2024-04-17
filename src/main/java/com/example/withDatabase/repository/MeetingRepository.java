package com.example.withDatabase.repository;

import com.example.withDatabase.entity.Meeting;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.util.Date;
import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting,Long> {

    @Query(value = "SELECT * FROM meeting WHERE meeting.employee_name = ?1 AND meeting.meeting_date = ?2 AND ((meeting.meeting_start_time <= ?3 AND meeting.meeting_end_time >= ?4) OR (meeting.meeting_start_time <= ?3 AND meeting.meeting_end_time >= ?3) OR (meeting.meeting_start_time >= ?3 AND meeting.meeting_start_time <= ?4) OR (meeting.meeting_start_time >= ?3 AND meeting.meeting_end_time <= ?4))", nativeQuery = true)
    List<Meeting> anotherMeetingExists(String employeeName, Date meetingDate, Time meetingStartTime, Time meetingEndTime);

    @Query(value = "SELECT * FROM meeting WHERE meeting.meeting_date = ?1 AND ((meeting.meeting_start_time <= ?2 AND meeting.meeting_end_time >= ?3) OR (meeting.meeting_start_time <= ?2 AND meeting.meeting_end_time >= ?2) OR (meeting.meeting_start_time >= ?2 AND meeting.meeting_start_time <= ?3) OR (meeting.meeting_start_time >= ?2 AND meeting.meeting_end_time <= ?3))", nativeQuery = true)
    List<Meeting> checkMeetingConflict(Date meetingDate, Time meetingStartTime, Time meetingEndTime);

    @Query(value = "SELECT * FROM meeting WHERE meeting.meeting_date = ?3 AND (meeting.employee_name = ?1 OR meeting.employee_name = ?2) ORDER BY meeting.meeting_start_time", nativeQuery = true)
    List<Meeting> checkForFreeSlots(String employee1, String employee2,Date date);

}
