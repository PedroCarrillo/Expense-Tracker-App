package com.pedrocarrillo.expensetracker.ui;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.pedrocarrillo.expensetracker.interfaces.IFragmentListener;

/**
 * Created by Pedro on 15/9/2015.
 */
public class BaseFragment extends Fragment{

    protected IFragmentListener mFragmentListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mFragmentListener = (IFragmentListener)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentListener = null;
    }
}
