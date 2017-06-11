package com.pedrocarrillo.expensetracker.presenters;

import com.pedrocarrillo.expensetracker.contract.LoginContract;

/**
 * Created by Xinyu Jin (Vicki) on 6/11/17.
 */

public class LoginPresenter extends BasePresenter<LoginContract.View> implements LoginContract.Presenter{

    public LoginPresenter(LoginContract.View view) {
        super(view);
    }

    @Override
    public void subscribe() {

    }


    @Override
    public void fbLoginClicked() {

    }

    @Override
    public void loginClicked(String userName, String password) {

    }
}
