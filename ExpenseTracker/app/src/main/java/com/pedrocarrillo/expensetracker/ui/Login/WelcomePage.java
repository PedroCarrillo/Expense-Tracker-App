package com.pedrocarrillo.expensetracker.ui.login;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Pedro on 15/9/2015.
 */
public class WelcomePage extends Fragment {

    final static String LAYOUT_ID = "layoutid";

    public static WelcomePage newInstance(int layoutId) {
        WelcomePage pane = new WelcomePage();
        Bundle args = new Bundle();
        args.putInt(LAYOUT_ID, layoutId);
        pane.setArguments(args);
        return pane;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(getArguments().getInt(LAYOUT_ID, -1), container, false);
        return rootView;
    }

}
