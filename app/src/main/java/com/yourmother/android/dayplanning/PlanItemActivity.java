package com.yourmother.android.dayplanning;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import java.util.Date;
import java.util.UUID;

public class PlanItemActivity extends SingleFragmentActivity {

    public static final String EXTRA_DATE =
            "com.yourmother.android.dayplanning.date";
    public static final String EXTRA_ITEM_ID =
            "com.yourmother.android.dayplanning.item_id";

    @NonNull
    public static Intent newIntent(Context packageContext, Date date, UUID itemId) {
        Intent intent = new Intent(packageContext, PlanItemActivity.class);
        intent.putExtra(EXTRA_DATE, date);
        intent.putExtra(EXTRA_ITEM_ID, itemId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        Date date = (Date) getIntent().getSerializableExtra(EXTRA_DATE);
        UUID itemId = (UUID) getIntent().getSerializableExtra(EXTRA_ITEM_ID);

        return PlanItemFragment.newInstance(date, itemId);
    }
}
