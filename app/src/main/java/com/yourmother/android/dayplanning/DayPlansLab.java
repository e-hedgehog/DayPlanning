package com.yourmother.android.dayplanning;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.yourmother.android.dayplanning.database.DayPlanningBaseHelper;
import com.yourmother.android.dayplanning.database.DayPlanningCursorWrapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.yourmother.android.dayplanning.database.DayPlanningDbSchema.DayPlansTable;
import static com.yourmother.android.dayplanning.database.DayPlanningDbSchema.PlanItemsTable;

public class DayPlansLab {

    private static final String TAG = "DayPlansLab";

    private static DayPlansLab sDayPlansLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static DayPlansLab get(Context context) {
        if (sDayPlansLab == null)
            sDayPlansLab = new DayPlansLab(context);
        return sDayPlansLab;
    }

    private DayPlansLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new DayPlanningBaseHelper(mContext)
                .getWritableDatabase();
    }

    public List<PlanItem> getDatePlansList(Date date) {
        List<PlanItem> items = new ArrayList<>();

        try (DayPlanningCursorWrapper cursor = queryPlans(
                DayPlansTable.Cols.DATE + " = ?",
                new String[]{String.valueOf(date.getTime())}
        )) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                items.add(cursor.getPlanItem());
                cursor.moveToNext();
            }
        }

        return items;
    }

    public PlanItem getPlanItem(Date date, UUID id) {
        try(DayPlanningCursorWrapper cursor = queryPlans(
                DayPlansTable.Cols.DATE + " = ? and " +
                        PlanItemsTable.Cols.UUID + " = ?",
                new String[]{String.valueOf(date.getTime()), id.toString()}
        )) {
            if (cursor.getCount() == 0)
                return null;

            cursor.moveToFirst();
            return cursor.getPlanItem();
        }
    }

    public void deletePlanItem(Date date, PlanItem item) {
        mDatabase.delete(
                PlanItemsTable.NAME,
                PlanItemsTable.Cols.DATE_ID + " = ? and " +
                        PlanItemsTable.Cols.UUID + " = ?",
                new String[]{String.valueOf(getDateId(date)), item.getId().toString()}
        );
    }

    public void deleteDatePlans(Date date) {
        mDatabase.delete(
                PlanItemsTable.NAME,
                PlanItemsTable.Cols.DATE_ID + " = ?",
                new String[]{String.valueOf(getDateId(date))}
        );
        mDatabase.delete(
                DayPlansTable.NAME,
                DayPlansTable.Cols.DATE + " = ?",
                new String[]{String.valueOf(date.getTime())}
        );
    }

    public void deleteDatePlansBefore(Date date) {
        try (DayPlanningCursorWrapper cursor = queryDates(
                DayPlansTable.Cols.DATE + " < ?",
                new String[]{String.valueOf(date.getTime())}
        )) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                deleteDatePlans(cursor.getDate());
                cursor.moveToNext();
            }
        }
    }

    public void updatePlanItem(Date date, PlanItem item) {
        String uuidString = item.getId().toString();
        ContentValues values = getContentValues(date, item);

        mDatabase.update(PlanItemsTable.NAME, values,
                PlanItemsTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    public void addPlanItem(Date date, PlanItem item) {
        if (getDateId(date) == 0)
            addDate(date);

        ContentValues values = getContentValues(date, item);
        mDatabase.insert(PlanItemsTable.NAME, null, values);
    }

    private void addDate(Date date) {
        ContentValues values = getDateContentValues(date);

        mDatabase.insert(DayPlansTable.NAME, null, values);
    }

    private int getDateId(Date date) {
        try (DayPlanningCursorWrapper cursor = queryDates(
                DayPlansTable.Cols.DATE + " = ?",
                new String[]{String.valueOf(date.getTime())}
        )) {
            Log.i(TAG, "getDateId(): " + date.toString());
            Log.i(TAG, cursor.getCount() == 0 ? "count = 0" : "count = ne 0");
            if (cursor.getCount() == 0)
                return 0;

            cursor.moveToFirst();
            return cursor.getDateId();
        }
    }

    private DayPlanningCursorWrapper queryPlans(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                DayPlansTable.NAME + " join " + PlanItemsTable.NAME +
                        " using(" + PlanItemsTable.Cols.DATE_ID + ")",
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new DayPlanningCursorWrapper(cursor);
    }

    private DayPlanningCursorWrapper queryDates(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                DayPlansTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new DayPlanningCursorWrapper(cursor);
    }

    private ContentValues getContentValues(Date date, PlanItem planItem) {
        ContentValues values = new ContentValues();
        values.put(PlanItemsTable.Cols.DATE_ID, getDateId(date));
        values.put(PlanItemsTable.Cols.UUID, planItem.getId().toString());
        values.put(PlanItemsTable.Cols.TITLE, planItem.getTitle());
        Date time = planItem.getTime();
        values.put(PlanItemsTable.Cols.TIME, time == null ? 0 : time.getTime());
        values.put(PlanItemsTable.Cols.TEXT, planItem.getText());
        values.put(PlanItemsTable.Cols.IS_ALARM_ON, planItem.isAlarmOn() ? 1 : 0);
        values.put(PlanItemsTable.Cols.STATUS, planItem.getStatus().getValue());
        return values;
    }

    private ContentValues getDateContentValues(Date date) {
        ContentValues values = new ContentValues();
        values.put(DayPlansTable.Cols.DATE, date.getTime());
        return values;
    }
}
