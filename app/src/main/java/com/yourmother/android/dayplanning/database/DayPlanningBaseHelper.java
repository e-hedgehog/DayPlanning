package com.yourmother.android.dayplanning.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.yourmother.android.dayplanning.database.DayPlanningDbSchema.*;

public class DayPlanningBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "dayPlanningBase.db";

    public DayPlanningBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DayPlansTable.NAME + "(" +
                DayPlansTable.Cols.DATE_ID + " integer primary key autoincrement, " +
                DayPlansTable.Cols.DATE + " integer" +
                ")"
        );

        db.execSQL("create table " + PlanItemsTable.NAME + "(" +
                "item_id integer primary key autoincrement, " +
                PlanItemsTable.Cols.DATE_ID + " integer, " +
                PlanItemsTable.Cols.UUID + ", " +
                PlanItemsTable.Cols.TITLE + ", " +
                PlanItemsTable.Cols.TIME + ", " +
                PlanItemsTable.Cols.TEXT + ", " +
                PlanItemsTable.Cols.IS_ALARM_ON + ", " +
                "foreign key (" + PlanItemsTable.Cols.DATE_ID + ") " +
                "references " + DayPlansTable.NAME + "(" + DayPlansTable.Cols.DATE_ID + ")" +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
