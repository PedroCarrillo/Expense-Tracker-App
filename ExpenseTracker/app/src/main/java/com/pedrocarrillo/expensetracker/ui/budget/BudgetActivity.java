package com.pedrocarrillo.expensetracker.ui.budget;

import android.os.Bundle;

import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.ui.BaseActivity;

/**
 * Created by PedroCarrillo on 6/3/16.
 */

public class BudgetActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        replaceFragment(BudgetFragment.newInstance(), false);
    }

}
