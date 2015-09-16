package com.pedrocarrillo.expensetracker.ui.Login;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.adapters.WelcomePagerAdapter;
import com.pedrocarrillo.expensetracker.ui.BaseFragment;
import com.pedrocarrillo.expensetracker.ui.Custom.CrossfadePageTransformer;

public class LoginActivityFragment extends BaseFragment {

    private ViewPager vpWelcome;

    public static LoginActivityFragment newInstance() {
        return new LoginActivityFragment();
    }

    public LoginActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        vpWelcome = (ViewPager)rootView.findViewById(R.id.vp_welcome);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        WelcomePagerAdapter welcomePagerAdapter = new WelcomePagerAdapter(getChildFragmentManager());
        vpWelcome.setAdapter(welcomePagerAdapter);
        vpWelcome.setPageTransformer(true, new CrossfadePageTransformer());
    }

    public void signIn(View v) {
        Log.e("hola", " hola12123");
    }
}
