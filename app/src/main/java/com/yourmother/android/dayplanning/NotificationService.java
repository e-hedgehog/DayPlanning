package com.yourmother.android.dayplanning;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.Date;

public class NotificationService {

    public static void setAlarm(Context context, Date date, PlanItem planItem, boolean isOn) {
        AlarmManager manager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        Intent intent = NotificationReceiver.newIntent(context, date, planItem);
        PendingIntent pi = PendingIntent
                .getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        if (isOn) {
            manager.cancel(pi);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            Calendar time = Calendar.getInstance();
            time.setTime(planItem.getTime());
            calendar.set(Calendar.SECOND, time.get(Calendar.SECOND));
            calendar.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
            calendar.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));

            manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() -
                    AlarmManager.INTERVAL_FIFTEEN_MINUTES, pi);
        } else {
            manager.cancel(pi);
            pi.cancel();
        }

        planItem.setAlarmOn(isOn);
        if (isOn)
            planItem.setStatus(PlanItemStatus.UPCOMING);
        DayPlansLab.get(context).updatePlanItem(date, planItem);
    }
}
