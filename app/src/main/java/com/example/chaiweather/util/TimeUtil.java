package com.example.chaiweather.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
    public static Date parseStringToDate(String dateString){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static long durationDate1ToDate2(Date date1,Date date2){
        return date2.getTime() - date1.getTime();
    }

    public static boolean isLargeThan3Hour(long mill){
        long onHour = 1000 * 60 * 60;
        return mill/onHour > 3;
    }
}
