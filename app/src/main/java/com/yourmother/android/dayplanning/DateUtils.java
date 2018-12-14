package com.yourmother.android.dayplanning;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static boolean isTimeAfterNow(Date date, PlanItem planItem) {
        Calendar calendar = Calendar.getInstance();
        Date currentTime = calendar.getTime();
        calendar.setTime(date);
        if (planItem.getTime() != null) {
            calendar.set(Calendar.HOUR_OF_DAY, planItem.getTime().getHours());
            calendar.set(Calendar.MINUTE, planItem.getTime().getMinutes());
            calendar.set(Calendar.SECOND, planItem.getTime().getSeconds());
        }

        return calendar.getTime().after(currentTime);
    }

    public static long differenceBetweenSetAndCurrentTime(Date date, Date time) {
        Calendar calendar = Calendar.getInstance();
        Date currentTime = calendar.getTime();
        calendar.setTime(date);
        if (time != null) {
            calendar.set(Calendar.HOUR_OF_DAY, time.getHours());
            calendar.set(Calendar.MINUTE, time.getMinutes());
            calendar.set(Calendar.SECOND, time.getSeconds());
        }

        return calendar.getTimeInMillis() - currentTime.getTime();
    }

    public static Calendar filterDate(Calendar calendar) {
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return calendar;
    }
}
