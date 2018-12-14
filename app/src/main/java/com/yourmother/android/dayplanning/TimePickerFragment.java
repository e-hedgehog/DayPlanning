package com.yourmother.android.dayplanning;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class TimePickerFragment extends DialogFragment {
    private static final String TAG = "TimePickerFragment";

    public static final String EXTRA_TIME =
            "com.yourmother.android.dayplanning.time";
    private static final String ARG_TIME = "time";

    private TimePicker mTimePicker;

    public static TimePickerFragment newInstance(Date time) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, time);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Date time = (Date) getArguments().getSerializable(ARG_TIME);

        Calendar calendar = Calendar.getInstance();

        View v = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_time, null);

        mTimePicker = v.findViewById(R.id.dialog_time_picker);
        calendar.setTime(time == null ? new Date() : time);

        String language = Locale.getDefault().getLanguage();
        if (language.equals("ru") || language.equals("uk"))
            mTimePicker.setIs24HourView(true);

        mTimePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
        mTimePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.select_time_dialog)
                .setPositiveButton(android.R.string.ok,
                        (dialog, which) -> {
                            int minute = mTimePicker.getCurrentMinute();
                            int hour = mTimePicker.getCurrentHour();
                            Date pickedTime = new GregorianCalendar(
                                    0, 0, 0, hour, minute).getTime();
                            sendResult(Activity.RESULT_OK, pickedTime);
                        })
                .create();
    }

    private void sendResult(int resultCode, Date time) {
        if (getTargetFragment() == null)
            return;

        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, time);

        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
