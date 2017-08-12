package com.pedrocarrillo.expensetracker.ui.login;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.contract.LoginContract;
import com.pedrocarrillo.expensetracker.ui.BaseButterKnifeFragment;

import butterknife.BindView;
import io.reactivex.functions.Consumer;

/**
 * Created by Xinyu Jin (Vicki) on 6/11/17.
 */

public class LoginFragment extends BaseButterKnifeFragment<LoginContract.Presenter> implements LoginContract.View {


    @BindView(R.id.email)
    TextInputEditText etEmail;
    @BindView(R.id.password)
    TextInputEditText etPassword;
    @BindView(R.id.btn_fb)
    Button btFb;
    @BindView(R.id.btn_login)
    Button btLogin;
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

    @Override
    protected void initView() {
        mSubscriptions.addAll(
                RxTextView.textChanges(etEmail)
                        .subscribe(charSequence -> mPresenter.setEmail(charSequence)),
                RxTextView.textChanges(etPassword)
                        .subscribe(charSequence -> mPresenter.setPassword(charSequence)),
                RxView.clicks(btLogin)
                        .subscribe(o -> {mPresenter.loginClicked();}),
                RxView.clicks(btFb)
                        .subscribe(o -> {mPresenter.fbLoginClicked();})
        );

    }
}
