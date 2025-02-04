package com.github.goplay.utils;

import java.sql.Timestamp;

public class CommonUtils {
    public static java.sql.Timestamp curTime(){
        return new Timestamp(System.currentTimeMillis());
    }
}
