package com.yourmother.android.dayplanning.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.yourmother.android.dayplanning.PlanItem;

import java.util.Date;
import java.util.UUID;

import static com.yourmother.android.dayplanning.database.DayPlanningDbSchema.*;

public class DayPlanningCursorWrapper extends CursorWrapper {
    public DayPlanningCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public PlanItem getPlanItem() {
        String uuidString = getString(getColumnIndex(PlanItemsTable.Cols.UUID));
        String title = getString(getColumnIndex(PlanItemsTable.Cols.TITLE));
        long time = getLong(getColumnIndex(PlanItemsTable.Cols.TIME));
        String text = getString(getColumnIndex(PlanItemsTable.Cols.TEXT));
        int isAlarmOn = getInt(getColumnIndex(PlanItemsTable.Cols.IS_ALARM_ON));

        PlanItem item = new PlanItem(UUID.fromString(uuidString));
        item.setTitle(title);
        item.setTime(time == 0 ? null : new Date(time));
        item.setText(text);
        item.setAlarmOn(isAlarmOn != 0);

        return item;
    }

    public Date getDate() {
        long date = getLong(getColumnIndex(DayPlansTable.Cols.DATE));
        return new Date(date);
    }

    public int getDateId() {
        return getInt(getColumnIndex(DayPlansTable.Cols.DATE_ID));
    }
}
