package com.pedrocarrillo.expensetracker.presenters;

import com.pedrocarrillo.expensetracker.interfaces.IBaseView;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Xinyu Jin (Vicki) on 6/11/17.
 */

public class BasePresenter<V extends IBaseView> {

    protected CompositeDisposable mSubscription;

    protected V mView;

    public BasePresenter(V view) {
        mSubscription = new CompositeDisposable();

        mView = view;
        mView.setPresenter(this);
    }

    public void unSubscribe() {
        mSubscription.clear();
    }
}
