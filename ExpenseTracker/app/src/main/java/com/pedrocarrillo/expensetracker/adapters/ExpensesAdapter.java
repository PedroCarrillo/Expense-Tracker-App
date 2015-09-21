package com.pedrocarrillo.expensetracker.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.pedrocarrillo.expensetracker.ExpenseTrackerApp;
import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.entities.Expense;
import com.pedrocarrillo.expensetracker.interfaces.IExpensesType;

import java.util.List;

/**
 * Created by pcarrillo on 21/09/2015.
 */
public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.ViewHolder> {

    private List<Expense> mExpensesList;
    private int lastPosition = -1;
    private int colorExpense;
    private int colorIncome;

    public ExpensesAdapter(Context context, List<Expense> mExpensesList) {
        this.mExpensesList = mExpensesList;
        this.colorExpense = context.getResources().getColor(R.color.colorAccentRed);
        this.colorIncome = context.getResources().getColor(R.color.colorAccentGreen);
    }

    @Override
    public ExpensesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_expense_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Expense expense = mExpensesList.get(position);
        holder.tvCategory.setText(expense.getCategory().getName());
        if (expense.getDescription() != null) {
            holder.tvDescription.setText(expense.getDescription());
            holder.tvDescription.setVisibility(View.VISIBLE);
        } else {
            holder.tvDescription.setVisibility(View.GONE);
        }
        switch (expense.getType()) {
            case IExpensesType.MODE_EXPENSES:
                holder.tvTotal.setTextColor(colorExpense);
                break;
            case IExpensesType.MODE_INCOME:
                holder.tvTotal.setTextColor(colorIncome);
                break;
        }
        holder.tvTotal.setText(String.valueOf(expense.getTotal()));
        holder.itemView.setTag(expense);
        setAnimation(holder, position);
    }

    @Override
    public int getItemCount() {
        return mExpensesList.size();
    }

    public void updateExpenses(List<Expense> mExpensesList) {
        this.mExpensesList = mExpensesList;
        notifyDataSetChanged();
    }

    private void setAnimation(ViewHolder holder, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(ExpenseTrackerApp.getContext(), R.anim.push_left_in);
            holder.itemView.startAnimation(animation);
            lastPosition = position;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvCategory;
        public TextView tvDescription;
        public TextView tvTotal;

        public ViewHolder(View v) {
            super(v);
            tvCategory = (TextView)v.findViewById(R.id.tv_category);
            tvDescription = (TextView)v.findViewById(R.id.tv_description);
            tvTotal = (TextView)v.findViewById(R.id.tv_total);
        }

    }

}