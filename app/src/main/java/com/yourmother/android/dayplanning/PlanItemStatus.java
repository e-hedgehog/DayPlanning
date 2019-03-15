package com.yourmother.android.dayplanning;

public enum PlanItemStatus {
    DONE(0), FAILED(1), UPCOMING(2), UNDEFINED(3);

    private int mValue;

    PlanItemStatus(int value) {
        mValue = value;
    }

    public int getValue() {
        return mValue;
    }

    public static PlanItemStatus fromInt(int i) {
        for (PlanItemStatus status : PlanItemStatus.values())
            if (status.getValue() == i)
                return status;
        return null;
    }
}
