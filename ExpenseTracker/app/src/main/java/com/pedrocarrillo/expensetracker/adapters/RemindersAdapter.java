package com.pedrocarrillo.expensetracker.adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.pedrocarrillo.expensetracker.ExpenseTrackerApp;
import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.entities.Reminder;
import com.pedrocarrillo.expensetracker.utils.Util;

import java.util.List;

/**
 * Created by Pedro on 9/26/2015.
 */
public class RemindersAdapter extends RecyclerView.Adapter<RemindersAdapter.ViewHolder> {

    private List<Reminder> mReminderList;
    private int lastPosition = -1;
    final private RemindersAdapterOnClickHandler mClickHandler;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

        public TextView tvTitle;
        public TextView tvDate;
        public SwitchCompat scState;

        public ViewHolder(View v) {
            super(v);
            tvTitle = (TextView)v.findViewById(R.id.tv_name);
            tvDate = (TextView)v.findViewById(R.id.tv_date);
            scState = (SwitchCompat)v.findViewById(R.id.sc_reminder);
            scState.setOnCheckedChangeListener(this);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickHandler.onClick(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(this.itemView.getTag() != null) {
                mClickHandler.onChecked(isChecked, this);
            }
        }
    }

    public RemindersAdapter(List<Reminder> mReminderList, RemindersAdapterOnClickHandler mClickHandler) {
        this.mReminderList = mReminderList;
        this.mClickHandler = mClickHandler;
    }

    @Override
    public RemindersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_reminder_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Reminder reminder = mReminderList.get(position);
        if (reminder.getName() != null) holder.tvTitle.setText(reminder.getName());
        holder.tvDate.setText(reminder.getDay() + " - " + Util.formatDateToString(reminder.getDate(), "HH:mm"));
        holder.scState.setChecked(reminder.isState());
        holder.itemView.setTag(reminder);
        setAnimation(holder, position);
    }

    @Override
    public int getItemCount() {
        return mReminderList.size();
    }

    public void updateReminders(List<Reminder> mReminderList) {
        this.mReminderList = mReminderList;
        notifyDataSetChanged();
    }

    private void setAnimation(ViewHolder holder, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(ExpenseTrackerApp.getContext(), R.anim.push_left_in);
            holder.itemView.startAnimation(animation);
            lastPosition = position;
        }
    }

    public interface RemindersAdapterOnClickHandler {
        void onClick(ViewHolder vh);
        void onChecked(boolean checked, ViewHolder vh);
    }

}