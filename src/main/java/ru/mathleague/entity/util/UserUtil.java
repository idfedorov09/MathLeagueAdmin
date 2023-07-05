package ru.mathleague.entity.util;

import java.time.LocalTime;
import java.util.Date;

public class UserUtil {

    private static LocalTime TIMEOUT = LocalTime.of(0, 5, 0);

    private static long dateAbsoluteDifferent(Date date1, Date date2){
        if(date1==null || date2==null) return 0;
        return Math.abs(date1.getTime() - date2.getTime());
    }

    public static boolean isTimeout(Date lastUserRequest){
        return dateAbsoluteDifferent(lastUserRequest, new Date())>=( TIMEOUT.toNanoOfDay() / 1_000_000 );
    }
}
