package com.yourmother.android.dayplanning;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class PlanItem implements Serializable {
    private UUID mId;
    private String mTitle;
    private String mText;
    private Date mTime;
    private boolean isAlarmOn;
    private PlanItemStatus mStatus;

    public PlanItem() {
        this(UUID.randomUUID());
        mStatus = PlanItemStatus.UNDEFINED;
    }

    public PlanItem(UUID id) {
        mId = id;
//        mTime = new Date();
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public Date getTime() {
        return mTime;
    }

    public void setTime(Date time) {
        mTime = time;
    }

    public boolean isAlarmOn() {
        return isAlarmOn;
    }

    public void setAlarmOn(boolean alarmOn) {
        isAlarmOn = alarmOn;
    }

    public PlanItemStatus getStatus() {
        return mStatus;
    }

    public void setStatus(PlanItemStatus status) {
        mStatus = status;
    }

    @Override
    public String toString() {
        return mTitle;
    }

}
