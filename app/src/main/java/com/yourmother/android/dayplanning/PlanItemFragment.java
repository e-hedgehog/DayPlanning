package com.yourmother.android.dayplanning;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

public class PlanItemFragment extends Fragment {

    private static final String TAG = "PlanItemFragment";

    private static final String ARG_DATE = "date";
    private static final String ARG_ITEM_ID = "item_id";

    private static final String DIALOG_TIME = "DialogTime";

    private static final int REQUEST_TIME = 0;

    private EditText mTitleField;
    private EditText mDetailsField;
    private Button mTimePickerButton;
    private Switch mNotificationSwitch;

    private Date mDate;
    private PlanItem mPlanItem;

    public static PlanItemFragment newInstance(Date date, UUID itemId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        args.putSerializable(ARG_ITEM_ID, itemId);

        PlanItemFragment fragment = new PlanItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mDate = (Date) getArguments().getSerializable(ARG_DATE);
        UUID itemId = (UUID) getArguments().getSerializable(ARG_ITEM_ID);

        mPlanItem = DayPlansLab.get(getActivity()).getPlanItem(mDate, itemId);
        Log.i(TAG, mPlanItem == null ? "mPlanItem is null" : "mPlanItem is not null");

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            String newDate = DateFormat
                    .format(getString(R.string.date_format), mDate).toString();
            activity.getSupportActionBar().setTitle(newDate);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_plan_item, container, false);

        mTitleField = v.findViewById(R.id.plan_item_title);
        mTitleField.setText(mPlanItem.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPlanItem.setTitle(s.toString());
                updatePlanItem();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        mDetailsField = v.findViewById(R.id.plan_item_details);
        mDetailsField.setText(mPlanItem.getText());
        mDetailsField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPlanItem.setText(s.toString());
                updatePlanItem();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        mTimePickerButton = v.findViewById(R.id.time_picker_button);
        updateTime();
        mTimePickerButton.setOnClickListener(view -> {
            FragmentManager manager = getFragmentManager();
            TimePickerFragment dialog = TimePickerFragment.newInstance(mPlanItem.getTime());
            dialog.setTargetFragment(PlanItemFragment.this, REQUEST_TIME);
            if (manager != null)
                dialog.show(manager, DIALOG_TIME);

        });

        mNotificationSwitch = v.findViewById(R.id.notification_switch);
        mNotificationSwitch.setChecked(mPlanItem.isAlarmOn());
        updateSwitch();
        mNotificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                NotificationService.setAlarm(getActivity(), mDate, mPlanItem, isChecked));

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        updatePlanItem();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_plan_item, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_item:
                DayPlansLab.get(getActivity()).deletePlanItem(mDate, mPlanItem);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;

        if (requestCode == REQUEST_TIME) {
            Date time = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mPlanItem.setTime(time);
            updateTime();
            updatePlanItem();
            updateSwitch();
            NotificationService
                    .setAlarm(getActivity(), mDate, mPlanItem, mNotificationSwitch.isChecked());
        }
    }

    private void updateSwitch() {

        boolean isAfter = DateUtils.isTimeAfterNow(mDate, mPlanItem);
        mNotificationSwitch.setEnabled(mPlanItem.getTime() != null && isAfter);

        if (!isAfter) {
            mNotificationSwitch.setChecked(false);
            mPlanItem.setAlarmOn(false);
        }
    }

    private void updatePlanItem() {
        DayPlansLab.get(getActivity()).updatePlanItem(mDate, mPlanItem);
    }

    private void updateTime() {
        Date time = mPlanItem.getTime();
        if (time != null) {
            String timeString = DateFormat
                    .format(getString(R.string.time_format), time).toString();
            mTimePickerButton.setText(timeString);

            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity != null)
                activity.getSupportActionBar().setSubtitle(timeString);
        }
    }

}
