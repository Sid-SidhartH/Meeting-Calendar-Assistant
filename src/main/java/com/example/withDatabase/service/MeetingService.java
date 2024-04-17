package com.example.withDatabase.service;

import com.example.withDatabase.entity.FreeSlotsFinder;
import com.example.withDatabase.entity.Meeting;
import com.example.withDatabase.error.InvalidRequestException;
import com.example.withDatabase.error.MeetingNotPossibleException;
import com.example.withDatabase.helper.CalculateTimeInterval;
import com.example.withDatabase.repository.MeetingRepository;
import org.antlr.v4.runtime.misc.LogManager;
//import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MeetingService {

    @Autowired
    MeetingRepository meetingRepository;


    public Meeting createMeeting(Meeting meeting) throws MeetingNotPossibleException {

        if (meetingRepository.anotherMeetingExists(meeting.getEmployeeName(), meeting.getMeetingDate(), meeting.getMeetingStartTime(), meeting.getMeetingEndTime()).size() > 0) {
            throw new MeetingNotPossibleException("meeting not possible, employee have already meeting at given time ");
        }
        return meetingRepository.save(meeting);
    }

    public List<String> checkMeetingConflict(Meeting meeting) throws InvalidRequestException {

        if (meeting.getMeetingStartTime() == null || meeting.getMeetingEndTime() == null || meeting.getMeetingDate() == null) {
            throw new InvalidRequestException("Invalid Request, Please Provide Proper Details");
        } else {

            List<Meeting> meetingConflicts = meetingRepository.checkMeetingConflict(meeting.getMeetingDate(), meeting.getMeetingStartTime(), meeting.getMeetingEndTime());
            List<String> employeeNames = new ArrayList<>();

            for (Meeting meetingConflict : meetingConflicts) {
                employeeNames.add(meetingConflict.getEmployeeName());
            }
            return employeeNames;
        }
    }

    public List<String> checkFreeSlots(FreeSlotsFinder freeSlotsFinder) throws InvalidRequestException, ParseException {

        if (freeSlotsFinder.getEmployee1() == null || freeSlotsFinder.getEmployee2() == null || freeSlotsFinder.getEmployee1().isBlank() || freeSlotsFinder.getEmployee2().isBlank() || freeSlotsFinder.getMeetingDate() == null) {
            throw new InvalidRequestException("Invalid Request, Please Provide Proper Details");
        } else {
            List<String> freeTimeSlots = new ArrayList<>();

            List<Meeting> meetingDetails = meetingRepository.checkForFreeSlots(freeSlotsFinder.getEmployee1(), freeSlotsFinder.getEmployee2(), freeSlotsFinder.getMeetingDate());
            List<Pair<Time, Time>> busySlots = new ArrayList<>();
            for (Meeting meetingDetail : meetingDetails) {
                busySlots.add(Pair.of(meetingDetail.getMeetingStartTime(), meetingDetail.getMeetingEndTime()));
            }

            String earlyTime = "07:00:00";
            CalculateTimeInterval calculateTimeInterval = new CalculateTimeInterval(busySlots.get(0).getFirst().toString(), earlyTime);
            if (calculateTimeInterval.differenceInHours() > 1 || calculateTimeInterval.differenceInMinutes() > 30) {
                freeTimeSlots.add("There is a free time slot from 07:00 to " + busySlots.get(0).getFirst());
            }

            for (int i = 0; i < busySlots.size() - 1; i++) {

                CalculateTimeInterval timeInterval = new CalculateTimeInterval(busySlots.get(i).getSecond().toString(), busySlots.get(i + 1).getFirst().toString());
                if (calculateTimeInterval.differenceInHours() > 1 || calculateTimeInterval.differenceInMinutes() > 30) {
                    freeTimeSlots.add("There is a free time slot from " + busySlots.get(i).getSecond() + " to " + busySlots.get(i + 1).getFirst());
                }
            }
            return freeTimeSlots;
        }
    }
}