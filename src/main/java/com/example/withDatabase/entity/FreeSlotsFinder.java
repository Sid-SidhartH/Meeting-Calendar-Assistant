package com.example.withDatabase.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FreeSlotsFinder {

    private String employee1;
    private String employee2;
    private Date meetingDate;
}
