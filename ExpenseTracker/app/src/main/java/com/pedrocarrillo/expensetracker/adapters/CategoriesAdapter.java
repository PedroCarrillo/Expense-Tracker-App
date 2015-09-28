package com.pedrocarrillo.expensetracker.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.pedrocarrillo.expensetracker.ExpenseTrackerApp;
import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.entities.Category;

import java.util.List;

/**
 * Created by pcarrillo on 17/09/2015.
 */
public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private List<Category> mCategoryList;
    private int lastPosition = -1;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;

        public ViewHolder(View v) {
            super(v);
            tvTitle = (TextView)v.findViewById(R.id.tv_title);
        }

    }

    public CategoriesAdapter(List<Category> categoryList) {
        this.mCategoryList = categoryList;
    }

    @Override
    public CategoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_category_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Category category = mCategoryList.get(position);
        holder.tvTitle.setText(category.getName());
        holder.itemView.setTag(category);
        setAnimation(holder, position);
    }

    @Override
    public int getItemCount() {
        return mCategoryList.size();
    }

    public void updateCategories(List<Category> categoryList) {
        this.mCategoryList = categoryList;
        notifyDataSetChanged();
    }

    private void setAnimation(ViewHolder holder, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(ExpenseTrackerApp.getContext(), R.anim.push_left_in);
            holder.itemView.startAnimation(animation);
            lastPosition = position;
        }
    }

}