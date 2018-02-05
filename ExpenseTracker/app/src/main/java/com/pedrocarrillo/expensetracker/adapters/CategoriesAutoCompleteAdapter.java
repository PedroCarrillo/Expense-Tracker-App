package com.pedrocarrillo.expensetracker.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.entities.Category;

import java.util.List;


/**
 * Created by pcarrillo on 21/09/2015.
 */
public class CategoriesAutoCompleteAdapter extends ArrayAdapter<Category> {

    List<Category> categoriesList = null;
    //LayoutInflater inflater;

    public CategoriesAutoCompleteAdapter(Activity context) {
        super(context, android.R.layout.simple_dropdown_item_1line);
        this.categoriesList = Category.getCategoriesExpense();
        //this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return categoriesList.size();
    }

    @Nullable
    @Override
    public Category getItem(int position) {
        return categoriesList.get(position);
    }

    /*@Override
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
    }*/

}