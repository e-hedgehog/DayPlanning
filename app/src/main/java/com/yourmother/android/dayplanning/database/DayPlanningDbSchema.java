package com.yourmother.android.dayplanning.database;

public class DayPlanningDbSchema {
    public static final class DayPlansTable {
        public static final String NAME = "dayPlans";

        public static final class Cols {
            public static final String DATE_ID = "date_id";
            public static final String DATE = "date";
        }
    }

    public static final class PlanItemsTable {
        public static final String NAME = "planItems";

        public static final class Cols {
            public static final String DATE_ID = "date_id";
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String TIME = "time";
            public static final String TEXT = "text";
            public static final String IS_ALARM_ON = "is_alarm_on";
        }
    }
}
