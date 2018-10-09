package com.yourmother.android.dayplanning;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Calendar;
import java.util.Date;

import static java.lang.Math.abs;

public class DayPlanningActivity extends AppCompatActivity
        implements DayPlanningFragment.Callbacks{

    private static final String TAG = "DayPlanningActivity";

    private ViewPager mViewPager;
    private static Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_planning_pager);

        mViewPager = findViewById(R.id.day_planning_view_pager);

        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int i) {
                calendar = filterDate(Calendar.getInstance());
                calendar.add(Calendar.DATE, i);

                return DayPlanningFragment.newInstance(calendar.getTime());
            }

            @Override
            public int getCount() {
                return 4000;
            }
        });

        DayPlansLab.get(this)
                .deleteDatePlansBefore(filterDate(Calendar.getInstance()).getTime());
    }

    @Override
    public void onDateSelected(Date date) {
        DayPlanningFragment fragment =
                (DayPlanningFragment) getCurrentFragment();

        if (fragment != null) {
            Date currentDate = fragment.getPreviousDate();

            if (date.equals(currentDate))
                return;

            final int millisInDay = 1000 * 60 * 60 * 24;
            int daysBetweenDates = (int) ((date.getTime() - currentDate.getTime()) / millisInDay);

            mViewPager.setCurrentItem(daysBetweenDates + mViewPager.getCurrentItem());
        }
    }

    private Fragment getCurrentFragment() {
        int currentItem = mViewPager.getCurrentItem();
        FragmentStatePagerAdapter adapter =
                (FragmentStatePagerAdapter) mViewPager.getAdapter();

        if (adapter == null)
            return null;

        return adapter.getItem(currentItem);
    }

    private Calendar filterDate(Calendar calendar) {
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        return calendar;
    }
}
