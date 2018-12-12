package com.yourmother.android.dayplanning;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.DateFormat;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DayPlanningFragment extends Fragment {

    private static final String TAG = "DayPlanningFragment";
    private static final String ARG_DATE = "date";
    private static final int REQUEST_DATE = 0;
    private static final String DIALOG_DATE = "DialogDate";

//    private TextView mDateTextView;
    private TextView mEmptyView;
    private RecyclerView mRecyclerView;
    private PlanItemAdapter mAdapter;
    private FloatingActionButton mNewItemFAB;

    private Date mDate;
    private Callbacks mCallbacks;
    private static Date mPreviousDate = new Date();

    private boolean isButtonVisible = true;

    public interface Callbacks {
        void onDateSelected(Date date);
    }

    public static DayPlanningFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        DayPlanningFragment fragment = new DayPlanningFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mDate = (Date) getArguments().getSerializable(ARG_DATE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater
                .inflate(R.layout.fragment_day_planning, container, false);

//        mDateTextView = view.findViewById(R.id.date_text_view);
//        String date = DateFormat.format(getString(R.string.date_format), mDate).toString();
//        mDateTextView.setText(date);

        mEmptyView = view.findViewById(R.id.empty_view);
        mEmptyView.setOnClickListener(v -> newPlanItem());

        mRecyclerView = view.findViewById(R.id.plans_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mNewItemFAB = view.findViewById(R.id.new_item_fab);
        mNewItemFAB.setOnClickListener(v -> newPlanItem());

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy < 0 && !isButtonVisible) {
                    // Scroll Down
                    isButtonVisible = true;
                    mNewItemFAB.animate().setInterpolator(new AccelerateDecelerateInterpolator())
                            .setDuration(200)
                            .translationY(0);
                } else if (dy > 0 && isButtonVisible) {
                    // Scroll Up
                    int bottom = ((ViewGroup.MarginLayoutParams) mNewItemFAB
                            .getLayoutParams()).bottomMargin;
                    isButtonVisible = false;
                    mNewItemFAB.animate().setInterpolator(new AccelerateDecelerateInterpolator())
                            .setDuration(200)
                            .translationY(mNewItemFAB.getHeight() + bottom);
                }
            }
        });

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_day_planning, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.select_date:
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mDate);
                dialog.setTargetFragment(DayPlanningFragment.this, REQUEST_DATE);
                if (manager != null)
                    dialog.show(manager, DIALOG_DATE);
                return true;
            case R.id.clear:
                DayPlansLab.get(getActivity()).deleteDatePlans(mDate);
                updateUI();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;

        if (requestCode == REQUEST_DATE) {
            mPreviousDate = mDate;
            mDate = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCallbacks.onDateSelected(mDate);
//            updateUI();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public void updateUI() {
        DayPlansLab dayPlansLab = DayPlansLab.get(getActivity());
        List<PlanItem> planItems = dayPlansLab.getDatePlansList(mDate);

        if (mAdapter == null) {
            mAdapter = new PlanItemAdapter(planItems);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setPlanItems(planItems);
            mAdapter.notifyDataSetChanged();
        }

        mEmptyView.setVisibility(planItems.size() > 0 ? View.GONE : View.VISIBLE);
    }

    private void newPlanItem() {
        PlanItem item = new PlanItem();
        DayPlansLab.get(getActivity()).addPlanItem(mDate, item);
        updateUI();

        Intent intent = PlanItemActivity
                .newIntent(getActivity(), mDate, item.getId());
        startActivity(intent);
    }

    public Date getPreviousDate() {
        return mPreviousDate;
    }

    private class PlanItemHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{

        private TextView mTimeTextView;
        private TextView mTitleTextView;
        private ImageView mAlarmImage;
        private PlanItem mPlanItem;

        public PlanItemHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_plan, parent, false));
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);

            mTimeTextView = itemView.findViewById(R.id.plan_time);
            mTitleTextView = itemView.findViewById(R.id.plan_title);
            mAlarmImage = itemView.findViewById(R.id.alarm_image);
        }

        public void bind(PlanItem planItem) {
            mPlanItem = planItem;
            Date time = mPlanItem.getTime();
            if (time != null) {
                String timeString = DateFormat
                        .format(getString(R.string.time_format), time).toString();
                mTimeTextView.setText(timeString);
            }
            mTitleTextView.setText(mPlanItem.getTitle());

            boolean isAfter = DateUtils.isTimeAfterNow(mDate, mPlanItem);
            mAlarmImage.setVisibility(mPlanItem.isAlarmOn() && isAfter ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View v) {
            Intent intent = PlanItemActivity.newIntent(getActivity(), mDate, mPlanItem.getId());
            startActivity(intent);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(0, R.id.context_delete, 0, R.string.menu_delete)
                    .setOnMenuItemClickListener(this);

        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.context_delete:
                    DayPlansLab.get(getActivity()).deletePlanItem(mDate, mPlanItem);
                    updateUI();
                    return true;
                default:
                    return false;
            }
        }
    }

    private class PlanItemAdapter extends RecyclerView.Adapter<PlanItemHolder> {

        private List<PlanItem> mPlanItems;

        public PlanItemAdapter(List<PlanItem> planItems) {
            mPlanItems = planItems;
        }

        @NonNull
        @Override
        public PlanItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new PlanItemHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull PlanItemHolder holder, int position) {
            PlanItem planItem = mPlanItems.get(position);
            holder.bind(planItem);
        }

        @Override
        public int getItemCount() {
            return mPlanItems.size();
        }

        public void setPlanItems(List<PlanItem> planItems) {
            mPlanItems = planItems;
        }

    }

}
