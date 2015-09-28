package com.pedrocarrillo.expensetracker.ui.reminders;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.entities.Reminder;
import com.pedrocarrillo.expensetracker.interfaces.IUserActionsMode;
import com.pedrocarrillo.expensetracker.ui.BaseFragment;
import com.pedrocarrillo.expensetracker.utils.DialogManager;
import com.pedrocarrillo.expensetracker.utils.RealmManager;
import com.pedrocarrillo.expensetracker.utils.Util;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Pedro on 9/26/2015.
 */
public class NewReminderFragment extends BaseFragment implements View.OnClickListener{

    public static final String REMINDER_ID_KEY = "_reminder_id";

    private TextView tvDay;
    private TextView tvTime;
    private EditText etName;

    private int daySelected;
    private Date timeSelected;
    private Reminder mReminder;

    private @IUserActionsMode int mUserActionMode;

    static NewReminderFragment newInstance(@IUserActionsMode int mode, String reminderId) {
        NewReminderFragment newReminderFragment = new NewReminderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(IUserActionsMode.MODE_TAG, mode);
        if (reminderId != null) bundle.putString(REMINDER_ID_KEY, reminderId);
        newReminderFragment.setArguments(bundle);
        return newReminderFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = onCreateFragmentView(R.layout.fragment_dialog_new_reminder, inflater, container, true);
        tvDay = (TextView) rootView.findViewById(R.id.tv_day);
        tvTime = (TextView) rootView.findViewById(R.id.tv_time);
        etName = (EditText) rootView.findViewById(R.id.et_name);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mUserActionMode = getArguments().getInt(IUserActionsMode.MODE_TAG) == IUserActionsMode.MODE_CREATE ? IUserActionsMode.MODE_CREATE : IUserActionsMode.MODE_UPDATE;
        }
        setMode();
        tvDay.setOnClickListener(this);
        tvTime.setOnClickListener(this);
    }

    private void setMode() {
        if (IUserActionsMode.MODE_CREATE == mUserActionMode) {
            setTitle(getString(R.string.create_a_reminder));
            daySelected = 1;
            timeSelected = new Date();
        } else {
            setTitle(getString(R.string.edit_a_reminder));
            String reminderId = getArguments().getString(REMINDER_ID_KEY);
            mReminder = (Reminder) RealmManager.getInstance().findById(Reminder.class, reminderId);
            daySelected = mReminder.getDay();
            timeSelected = mReminder.getDate();
            etName.setText(mReminder.getName());
        }
        updateData();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_reminder, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            onSaveReminder();
        }
        return super.onOptionsItemSelected(item);
    }

    private void onSaveReminder() {
        if (!Util.isEmptyField(etName)) {
            if (mUserActionMode == IUserActionsMode.MODE_CREATE) {
                Reminder.saveNewReminder(etName.getText().toString(), daySelected, true, timeSelected);
            } else {
                Reminder updateReminder = new Reminder(etName.getText().toString(), daySelected, mReminder.isState(), timeSelected);
                updateReminder.setId(mReminder.getId());
                updateReminder.setCreatedAt(mReminder.getCreatedAt());
                Reminder.updateReminder(updateReminder, mReminder.isState());
            }
            mFragmentListener.setResultWithData(Activity.RESULT_OK, new Intent());
        } else {
            DialogManager.getInstance().showShortSnackBar(getView(), getString(R.string.reminder_name_validation));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_day) {
            DialogManager.getInstance().createSinglePickDialog(getActivity(), getString(R.string.pick_day_of_month), R.array.reminder_days_available, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String[] daysArray = getResources().getStringArray(R.array.reminder_days_available);
                    daySelected = Integer.valueOf(daysArray[which]);
                    tvDay.setText(String.valueOf(daySelected));
                }
            });
        } else if (v.getId() == R.id.tv_time) {
            Calendar c = Calendar.getInstance();
            c.setTime(timeSelected);
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            DialogManager.getInstance().createTimePickerDialog(getActivity(), hour, minute ,new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    timeSelected = calendar.getTime();
                    updateData();
                }
            });
        }
    }

    private void updateData() {
        Calendar time = Calendar.getInstance();
        time.setTime(timeSelected);
        tvDay.setText(String.valueOf(daySelected));
        tvTime.setText(getString(R.string.hour_format, time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE)));
    }

}