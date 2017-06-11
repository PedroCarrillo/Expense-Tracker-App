package com.pedrocarrillo.expensetracker.contract;

import com.pedrocarrillo.expensetracker.interfaces.IBaseView;
import com.pedrocarrillo.expensetracker.interfaces.IPresenter;

/**
 * Created by Xinyu Jin (Vicki) on 6/11/17.
 */

public class LoginContract {

    public interface Presenter extends IPresenter {

        void fbLoginClicked();

        void loginClicked(String userName, String password);
    }

    public interface View extends IBaseView<Presenter> {

        void showError(String error);

        void goToNext();
    }
}
