package com.yourmother.android.dayplanning;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;

import java.util.Date;
import java.util.UUID;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String EXTRA_DATE =
            "com.yourmother.android.dayplanning.date";
    public static final String EXTRA_ITEM_ID =
            "com.yourmother.android.dayplanning.item";

    public static Intent newIntent(Context context, Date date, PlanItem planItem) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra(EXTRA_DATE, date);
        intent.putExtra(EXTRA_ITEM_ID, planItem.getId());
        return intent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Date date = (Date) intent.getSerializableExtra(EXTRA_DATE);
        UUID id = (UUID) intent.getSerializableExtra(EXTRA_ITEM_ID);
        PlanItem item = DayPlansLab.get(context).getPlanItem(date, id);
        boolean differenceLessThanInterval =
                DateUtils.differenceBetweenSetAndCurrentTime(date, item.getTime()) <
                        AlarmManager.INTERVAL_FIFTEEN_MINUTES;
        int contentId = differenceLessThanInterval ?
                R.string.notification_text_now : R.string.notification_text;

        Intent i = PlanItemActivity.newIntent(context, date, id);
        PendingIntent pi = PendingIntent
                .getActivity(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);

        Resources resources = context.getResources();
        Notification notification = new NotificationCompat.Builder(context)
                .setTicker(resources.getString(R.string.notification_title))
                .setSmallIcon(android.R.drawable.ic_menu_today)
                .setContentTitle(resources.getString(R.string.notification_title))
                .setContentText(resources.getString(contentId, item.getTitle()))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .build();

        nm.notify(1, notification);

    }
}
