package com.github.goplay.utils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class CommonUtils {
    public static java.sql.Timestamp curTime(){
        return new Timestamp(System.currentTimeMillis());
    }

    public static Integer getDaysDiff(Timestamp start, Timestamp end) {
        // 将Timestamp转换为LocalDate
        LocalDate startDate = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = end.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        return (int) ChronoUnit.DAYS.between(startDate, endDate);
    }
}
