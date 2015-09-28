package com.pedrocarrillo.expensetracker.ui.reminders;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.adapters.RemindersAdapter;
import com.pedrocarrillo.expensetracker.custom.DefaultRecyclerViewItemDecorator;
import com.pedrocarrillo.expensetracker.entities.Reminder;
import com.pedrocarrillo.expensetracker.interfaces.IUserActionsMode;
import com.pedrocarrillo.expensetracker.ui.MainActivity;
import com.pedrocarrillo.expensetracker.ui.MainFragment;
import com.pedrocarrillo.expensetracker.utils.DialogManager;

import java.util.List;

/**
 * Created by pcarrillo on 17/09/2015.
 */
public class ReminderFragment extends MainFragment implements RemindersAdapter.RemindersAdapterOnClickHandler{

    public static final int RQ_REMINDER = 1002;

    private RecyclerView rvReminders;
    private List<Reminder> mReminderList;
    private RemindersAdapter mRemindersAdapter;

    public static ReminderFragment newInstance() {
        return new ReminderFragment();
    }

    public ReminderFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void onAddNewReminder() {
        Intent intent = new Intent(getActivity(), NewReminderActivity.class);
        intent.putExtra(IUserActionsMode.MODE_TAG, IUserActionsMode.MODE_CREATE);
        startActivityForResult(intent, RQ_REMINDER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reminders, container, false);
        rvReminders = (RecyclerView)rootView.findViewById(R.id.rv_reminders);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMainActivityListener.setMode(MainActivity.NAVIGATION_MODE_STANDARD);
        mMainActivityListener.setFAB(R.drawable.ic_add_white_48dp, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddNewReminder();
            }
        });
        mMainActivityListener.setTitle(getString(R.string.reminders));
        mReminderList = Reminder.getReminders();
        mRemindersAdapter = new RemindersAdapter(mReminderList, this);
        rvReminders.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvReminders.setAdapter(mRemindersAdapter);
        rvReminders.setHasFixedSize(true);
        rvReminders.addItemDecoration(new DefaultRecyclerViewItemDecorator(getResources().getDimension(R.dimen.dimen_10dp)));

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final Reminder reminder = (Reminder) viewHolder.itemView.getTag();
                DialogManager.getInstance().createCustomAcceptDialog(getActivity(), getString(R.string.delete), getString(R.string.confirm_reminder_delete), getString(R.string.confirm), getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            Reminder.eraseReminder(reminder);
                        }
                        reloadData();
                    }
                });
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rvReminders);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RQ_REMINDER && resultCode == Activity.RESULT_OK) {
            reloadData();
        }
    }

    @Override
    public void onClick(RemindersAdapter.ViewHolder vh) {
        Reminder reminder = (Reminder) vh.itemView.getTag();
        Intent intent = new Intent(getActivity(), NewReminderActivity.class);
        intent.putExtra(IUserActionsMode.MODE_TAG, IUserActionsMode.MODE_UPDATE);
        intent.putExtra(NewReminderFragment.REMINDER_ID_KEY, reminder.getId());
        startActivityForResult(intent, RQ_REMINDER);
    }

    @Override
    public void onChecked(boolean checked, RemindersAdapter.ViewHolder vh) {
        Reminder reminder = (Reminder) vh.itemView.getTag();
        if (reminder != null) {
            Reminder.updateReminder(reminder, checked);
        }
    }

    private void reloadData() {
        mReminderList = Reminder.getReminders();
        mRemindersAdapter.updateReminders(mReminderList);
    }
}