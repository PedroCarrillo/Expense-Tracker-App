package com.pedrocarrillo.expensetracker.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.contract.LoginContract;
import com.pedrocarrillo.expensetracker.ui.BaseButterKnifeFragment;

/**
 * Created by Xinyu Jin (Vicki) on 6/11/17.
 */

public class LoginFragment extends BaseButterKnifeFragment<LoginContract.Presenter> implements LoginContract.View {


    public static LoginFragment newInstance(){
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return onCreateFragmentView(R.layout.fragment_login, inflater, container, true);

    }


    @Override
    public void showError(String error) {

    }

    @Override
    public void goToNext() {

    }

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
