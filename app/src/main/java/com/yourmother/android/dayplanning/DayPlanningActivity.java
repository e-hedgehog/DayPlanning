package com.yourmother.android.dayplanning;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;

import java.util.Calendar;
import java.util.Date;

public class DayPlanningActivity extends AppCompatActivity
        implements DayPlanningFragment.Callbacks{

    private static final String TAG = "DayPlanningActivity";

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_planning_pager);

        mViewPager = findViewById(R.id.day_planning_view_pager);

        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int i) {
                Log.i(TAG, getDateInPosition(i).toString());
                return DayPlanningFragment.newInstance(getDateInPosition(i));
            }

            @Override
            public int getCount() {
                return 4000;
            }

            @NonNull
            @Override
            public CharSequence getPageTitle(int position) {
                return DateFormat.format(getString(R.string.date_format),
                        getDateInPosition(position)).toString();
            }
        });

        mViewPager.setPageTransformer(false, (page, position) -> {
            int pageWidth = page.getWidth();
            float absPosition = Math.abs(position);
            float pageWidthTimesPosition = pageWidth * position;

            if (position != 0.0f) {
                View fab = page.findViewById(R.id.new_item_fab);
                fab.setAlpha(1.0f - absPosition * 2.5f);

                if (position > -1.0f && position < 0.0f)
                    fab.setTranslationY(-pageWidthTimesPosition / 2.0f);
                else if (position > 0.0f && position < 1.0f)
                    fab.setTranslationY(pageWidthTimesPosition / 2.0f);
            }
        });

        DayPlansLab.get(this)
                .deleteDatePlansBefore(DateUtils.filterDate(Calendar.getInstance()).getTime());
    }

    @Override
    public void onDateSelected(Date date) {
            Date currentDate = getCurrentDate();

            if (date.equals(currentDate))
                return;

            final int millisInDay = 1000 * 60 * 60 * 24;
            int daysBetweenDates = (int) ((date.getTime() - currentDate.getTime()) / millisInDay);

            mViewPager.setCurrentItem(daysBetweenDates + mViewPager.getCurrentItem());
    }

    @Override
    public Date getCurrentDate() {
        return getDateInPosition(mViewPager.getCurrentItem());
    }

//    private Fragment getCurrentFragment() {
//        int currentItem = mViewPager.getCurrentItem();
//        FragmentStatePagerAdapter adapter =
//                (FragmentStatePagerAdapter) mViewPager.getAdapter();
//
//        if (adapter == null)
//            return null;
//
//        return adapter.getItem(currentItem);
//    }

    private Date getDateInPosition(int position) {
        Calendar calendar = DateUtils.filterDate(Calendar.getInstance());
        calendar.add(Calendar.DATE, position);
        return calendar.getTime();
    }
}
