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
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment {

    private static final String ARG_DATE = "date";
    private static final String ARG_PLAN_ITEM = "planItem";
    public static final String EXTRA_DATE =
            "com.yourmother.android.dayplanning.date";
    public static final String EXTRA_PLAN_ITEM =
            "com.yourmother.android.dayplanning.plan_item";

    private DatePicker mDatePicker;

    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static DatePickerFragment newInstance(Date date, PlanItem planItem) {
        DatePickerFragment fragment = newInstance(date);
        Bundle args = fragment.getArguments();
        if (args != null)
            args.putSerializable(ARG_PLAN_ITEM, planItem);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date date = (Date) getArguments().getSerializable(ARG_DATE);
        PlanItem planItem = (PlanItem) getArguments().getSerializable(ARG_PLAN_ITEM);

        Calendar calendar = Calendar.getInstance();

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_date, null);

        mDatePicker = v.findViewById(R.id.dialog_date_picker);
        mDatePicker.setMinDate(calendar.getTimeInMillis());
        calendar.setTime(date);
        mDatePicker.init(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                null
        );

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.select_date_dialog)
                .setPositiveButton(android.R.string.ok,
                        (dialogInterface, i) -> {
                            int year = mDatePicker.getYear();
                            int month = mDatePicker.getMonth();
                            int day = mDatePicker.getDayOfMonth();
                            Date pickedDate = new GregorianCalendar(year, month, day).getTime();
                            sendResult(Activity.RESULT_OK, pickedDate, planItem);
                        })
                .create();
    }

    private void sendResult(int resultCode, Date date, PlanItem planItem) {
        if (getTargetFragment() == null)
            return;

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);
        intent.putExtra(EXTRA_PLAN_ITEM, planItem);

        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
