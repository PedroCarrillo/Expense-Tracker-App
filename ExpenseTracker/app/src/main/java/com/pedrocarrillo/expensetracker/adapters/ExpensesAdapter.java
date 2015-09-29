package com.pedrocarrillo.expensetracker.adapters;

import android.content.Context;
import android.support.v4.view.ViewCompat;
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
import com.pedrocarrillo.expensetracker.interfaces.IDateMode;
import com.pedrocarrillo.expensetracker.interfaces.IExpensesType;
import com.pedrocarrillo.expensetracker.utils.Util;

import java.util.List;

/**
 * Created by pcarrillo on 21/09/2015.
 */
public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_EXPENSE_ROW = 1;
    private @IDateMode int mCurrentDateMode;

    private List<Expense> mExpensesList;
    private int lastPosition = -1;
    private int colorExpense;
    private int colorIncome;
    private String prefixExpense;
    private String prefixIncome;
    private String titleTransitionName;

    final private ExpenseAdapterOnClickHandler mClickHandler;

    public ExpensesAdapter(Context context, ExpenseAdapterOnClickHandler onClickHandler, List<Expense> mExpensesList, @IDateMode int mCurrentDateMode) {
        this.mExpensesList = mExpensesList;
        this.mClickHandler = onClickHandler;
        this.colorExpense = context.getResources().getColor(R.color.colorAccentRed);
        this.colorIncome = context.getResources().getColor(R.color.colorAccentGreen);
        this.prefixExpense = context.getResources().getString(R.string.expense_prefix);
        this.prefixIncome = context.getResources().getString(R.string.income_prefix);
        this.mCurrentDateMode = mCurrentDateMode;
        this.titleTransitionName = context.getString(R.string.tv_title_transition);
    }

    @Override
    public ExpensesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = -1;
        switch (viewType) {
            case VIEW_TYPE_HEADER: {
                layoutId = R.layout.layout_expense_header_item;
                break;
            }
            case VIEW_TYPE_EXPENSE_ROW: {
                layoutId = R.layout.layout_expense_item;
                break;
            }
        }
        View v = LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        switch (getItemViewType(position)) {
            case VIEW_TYPE_HEADER:
                float total = Expense.getTotalExpensesByDateMode(mCurrentDateMode);
                holder.tvTotal.setText(Util.getFormattedCurrency(total));
                break;
            case VIEW_TYPE_EXPENSE_ROW:
                final Expense expense = mExpensesList.get(position-1);
                String prefix = "";
                switch (expense.getType()) {
                    case IExpensesType.MODE_EXPENSES:
                        holder.tvTotal.setTextColor(colorExpense);
                        prefix = String.format(prefixExpense, Util.getFormattedCurrency(expense.getTotal()));
                        break;
                    case IExpensesType.MODE_INCOME:
                        holder.tvTotal.setTextColor(colorIncome);
                        prefix = String.format(prefixIncome, Util.getFormattedCurrency(expense.getTotal()));
                        break;
                }
                if (expense.getCategory() != null)holder.tvCategory.setText(expense.getCategory().getName());
                if (expense.getDescription() != null && !expense.getDescription().isEmpty()) {
                    holder.tvDescription.setText(expense.getDescription());
                    holder.tvDescription.setVisibility(View.VISIBLE);
                } else {
                    holder.tvDescription.setVisibility(View.GONE);
                }
                holder.tvTotal.setText(prefix);
                holder.itemView.setTag(expense);
                ViewCompat.setTransitionName(holder.tvTotal, titleTransitionName);
                break;
        }
        setAnimation(holder, position);
    }

    @Override
    public int getItemCount() {
        return mExpensesList.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_HEADER : VIEW_TYPE_EXPENSE_ROW;
    }

    public void updateExpenses(List<Expense> mExpensesList, @IDateMode int mCurrentDateMode) {
        this.mCurrentDateMode = mCurrentDateMode;
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

    public interface ExpenseAdapterOnClickHandler {
        void onClick(ViewHolder vh);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView tvCategory;
        public TextView tvDescription;
        public TextView tvTotal;

        public ViewHolder(View v) {
            super(v);
            tvCategory = (TextView)v.findViewById(R.id.tv_category);
            tvDescription = (TextView)v.findViewById(R.id.tv_description);
            tvTotal = (TextView)v.findViewById(R.id.tv_total);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (getAdapterPosition() != 0) mClickHandler.onClick(this);
        }
    }

}