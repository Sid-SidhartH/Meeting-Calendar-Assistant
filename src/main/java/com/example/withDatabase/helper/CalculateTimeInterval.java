package com.example.withDatabase.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CalculateTimeInterval {
    public String time1;
    public String time2;
    public Date date1;
    public Date date2;
    public long differenceInMilliSeconds;

    public CalculateTimeInterval(String time1, String time2) throws ParseException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        date1 = simpleDateFormat.parse(time1);
        date2 = simpleDateFormat.parse(time2);
        differenceInMilliSeconds  = Math.abs(date2.getTime() - date1.getTime());

    }

    public long differenceInHours() {
        return (differenceInMilliSeconds / (60 * 60 * 1000)) % 24;
    }

    public long differenceInMinutes() {
        return (differenceInMilliSeconds / (60 * 1000)) % 60;
    }

    public void message() {
        System.out.println(
                "Difference is " + this.differenceInHours() + " hours "
                        + this.differenceInMinutes() + " minutes ");
    }

}
