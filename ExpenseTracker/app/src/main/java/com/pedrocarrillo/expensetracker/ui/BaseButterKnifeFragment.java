package com.pedrocarrillo.expensetracker.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.pedrocarrillo.expensetracker.interfaces.IPresenter;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Xinyu Jin (Vicki) on 6/11/17.
 */

public abstract class BaseButterKnifeFragment<P extends IPresenter>
        extends BaseFragment {

    protected P mPresenter;
    protected CompositeDisposable mSubscriptions;
    protected Unbinder unbinder;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPresenter != null) {
            mPresenter.subscribe();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPresenter != null) {
            mPresenter.unSubscribe();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


}
