package com.pedrocarrillo.expensetracker.presenters;

import android.text.TextUtils;

import com.pedrocarrillo.expensetracker.contract.LoginContract;

/**
 * Created by Xinyu Jin (Vicki) on 6/11/17.
 */

public class LoginPresenter extends BasePresenter<LoginContract.View> implements LoginContract.Presenter{

    private CharSequence email;
    private CharSequence password;

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
    public void loginClicked() {
        if(isValidEmail(email) && !TextUtils.isEmpty(password)){
            //TODO call firebase login
        }else{
            mView.showError("Your Email or password is not valid");
        }
    }

    @Override
    public void setEmail(CharSequence email) {
        this.email = email;
    }

    @Override
    public void setPassword(CharSequence password) {
        this.password = password;
    }

    private boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }


}
