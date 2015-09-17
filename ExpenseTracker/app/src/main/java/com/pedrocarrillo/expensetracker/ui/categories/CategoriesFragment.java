package com.pedrocarrillo.expensetracker.ui.categories;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.adapters.CategoriesAdapter;
import com.pedrocarrillo.expensetracker.ui.BaseFragment;
import com.pedrocarrillo.expensetracker.ui.MainActivity;
import com.pedrocarrillo.expensetracker.ui.MainFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pcarrillo on 17/09/2015.
 */
public class CategoriesFragment extends MainFragment {

    private List<String> tabList = new ArrayList<>();
    private RecyclerView rvCategories;

    public static CategoriesFragment newInstance() {
        return new CategoriesFragment();
    }

    public CategoriesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivityListener.setMode(MainActivity.NAVIGATION_MODE_TABS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_categories, container, false);
        rvCategories = (RecyclerView)rootView.findViewById(R.id.rv_categories);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tabList.add("Expenses");
        tabList.add("Income");
        mMainActivityListener.setTabs(tabList);
        String[] testData = new String[] { "cat1", "cat2", "cat3", "cat4","cat5","cat6","cat7","cat8","cat9","cat10","cat11","cat12","cat13","cat14","cat15","cat16","cat8","cat9","cat10","cat11","cat12","cat13","cat14","cat15","cat16" ,"cat8","cat9","cat10","cat11","cat12","cat13","cat14","cat15","cat16" } ;
        rvCategories.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvCategories.setAdapter(new CategoriesAdapter(testData));
    }
}