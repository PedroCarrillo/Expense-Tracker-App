package com.pedrocarrillo.expensetracker.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.entities.Category;


/**
 * Created by pcarrillo on 21/09/2015.
 */
public class CategoriesSpinnerAdapter extends ArrayAdapter<Category> {

    Category[] categoriesList = null;
    LayoutInflater inflater;

    public CategoriesSpinnerAdapter(Activity context, Category[] categoriesList) {
        super(context, android.R.layout.simple_spinner_dropdown_item, categoriesList);
        this.categoriesList = categoriesList;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        View row = inflater.inflate(R.layout.layout_category_spinner_item, parent, false);
        Category category = categoriesList[position];
        TextView title = (TextView)row.findViewById(R.id.tv_title);
        title.setText(category.getName());
        return row;
    }

}