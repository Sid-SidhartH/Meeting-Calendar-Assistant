package com.example.withDatabase.Controller;

import com.example.withDatabase.entity.FreeSlotsFinder;
import com.example.withDatabase.entity.Meeting;
import com.example.withDatabase.error.InvalidRequestException;
import com.example.withDatabase.error.MeetingNotPossibleException;
import com.example.withDatabase.service.MeetingService;
import org.apache.el.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MeetingController {

    @Autowired
    MeetingService meetingService;

    @PostMapping("/createMeeting")
    public Meeting createMeetings(@RequestBody Meeting meeting) throws MeetingNotPossibleException {
        return meetingService.createMeeting(meeting);
    }
    @GetMapping("/requestMeeting")
    public List<String> checkMeetingConflict(@RequestBody Meeting meeting) throws InvalidRequestException {
        return meetingService.checkMeetingConflict(meeting);
    }
    @GetMapping("/checkFreeSlots")
    public List<String> checkFreeSlots(@RequestBody FreeSlotsFinder freeSlotsFinder) throws ParseException, InvalidRequestException, java.text.ParseException {
        return meetingService.checkFreeSlots(freeSlotsFinder);
    }


}
