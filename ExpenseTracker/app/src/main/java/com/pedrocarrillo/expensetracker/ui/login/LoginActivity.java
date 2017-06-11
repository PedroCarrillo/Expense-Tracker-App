package com.pedrocarrillo.expensetracker.ui.login;

import android.os.Bundle;
import android.util.Log;

import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.presenters.LoginPresenter;
import com.pedrocarrillo.expensetracker.ui.BaseActivity;

public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        LoginFragment fragment = LoginFragment.newInstance();
        new LoginPresenter(fragment);
        replaceFragment(LoginFragment.newInstance(),true);
    }

}
