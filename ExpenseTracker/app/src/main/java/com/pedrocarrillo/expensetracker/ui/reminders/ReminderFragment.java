package com.pedrocarrillo.expensetracker.ui.reminders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.ui.BaseFragment;
import com.pedrocarrillo.expensetracker.ui.MainActivity;
import com.pedrocarrillo.expensetracker.ui.MainFragment;

/**
 * Created by pcarrillo on 17/09/2015.
 */
public class ReminderFragment extends MainFragment {

    public static ReminderFragment newInstance() {
        return new ReminderFragment();
    }

    public ReminderFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivityListener.setMode(MainActivity.NAVIGATION_MODE_STANDARD);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reminders, container, false);
    }

}