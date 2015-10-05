package com.pedrocarrillo.expensetracker.ui.reminders;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.adapters.RemindersAdapter;
import com.pedrocarrillo.expensetracker.custom.DefaultRecyclerViewItemDecorator;
import com.pedrocarrillo.expensetracker.entities.Category;
import com.pedrocarrillo.expensetracker.entities.Reminder;
import com.pedrocarrillo.expensetracker.interfaces.IUserActionsMode;
import com.pedrocarrillo.expensetracker.ui.MainActivity;
import com.pedrocarrillo.expensetracker.ui.MainFragment;
import com.pedrocarrillo.expensetracker.utils.DialogManager;
import com.pedrocarrillo.expensetracker.utils.RealmManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pcarrillo on 17/09/2015.
 */
public class ReminderFragment extends MainFragment implements RemindersAdapter.RemindersAdapterOnClickHandler{

    public static final int RQ_REMINDER = 1002;

    private RecyclerView rvReminders;
    private List<Reminder> mReminderList;
    private RemindersAdapter mRemindersAdapter;
    private TextView tvEmpty;

    // Action mode for reminders.
    private ActionMode mActionMode;

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
        tvEmpty = (TextView)rootView.findViewById(R.id.tv_empty);
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
        reloadData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RQ_REMINDER && resultCode == Activity.RESULT_OK) {
            reloadData();
        }
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.expenses_context_menu, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete:
                    eraseReminders();
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mRemindersAdapter.clearSelection();
            mActionMode = null;
        }
    };

    private void eraseReminders() {
        DialogManager.getInstance().createCustomAcceptDialog(getActivity(), getString(R.string.delete), getString(R.string.confirm_reminder_delete), getString(R.string.confirm), getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    List<Reminder> remindersToDelete = new ArrayList<>();
                    for (int position : mRemindersAdapter.getSelectedItems()) {
                        remindersToDelete.add(mReminderList.get(position));
                    }
                    Reminder.eraseReminders(remindersToDelete);
                }
                reloadData();
                mActionMode.finish();
                mActionMode = null;
            }
        });
    }

    @Override
    public void onClick(RecyclerView.ViewHolder vh, int position) {
        if (mActionMode == null) {
            Reminder reminder = (Reminder) vh.itemView.getTag();
            Intent intent = new Intent(getActivity(), NewReminderActivity.class);
            intent.putExtra(IUserActionsMode.MODE_TAG, IUserActionsMode.MODE_UPDATE);
            intent.putExtra(NewReminderFragment.REMINDER_ID_KEY, reminder.getId());
            startActivityForResult(intent, RQ_REMINDER);
        } else {
            toggleSelection(position);
        }
    }

    @Override
    public void onLongClick(RecyclerView.ViewHolder vh, int position) {
        if (mActionMode == null) {
            mActionMode = mMainActivityListener.setActionMode(mActionModeCallback);
        }
        toggleSelection(position);
    }

    @Override
    public void onChecked(boolean checked, RemindersAdapter.ViewHolder vh) {
        Reminder reminder = (Reminder) vh.itemView.getTag();
        if (reminder != null) {
            Reminder.updateReminder(reminder, checked);
        }
    }

    public void toggleSelection(int position) {
        mRemindersAdapter.toggleSelection(position);
        int count = mRemindersAdapter.getSelectedItemCount();
        if (count == 0) {
            mActionMode.finish();
        } else {
            mActionMode.setTitle(String.valueOf(count));
            mActionMode.invalidate();
        }
    }

    private void reloadData() {
        mReminderList = Reminder.getReminders();
        if (mReminderList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
        mRemindersAdapter.updateReminders(mReminderList);
    }
}