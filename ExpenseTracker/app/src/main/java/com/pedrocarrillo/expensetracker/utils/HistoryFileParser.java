package com.pedrocarrillo.expensetracker.utils;

import com.pedrocarrillo.expensetracker.ExpenseTrackerApp;
import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.entities.Expense;
import com.pedrocarrillo.expensetracker.interfaces.FileGeneratorParser;
import com.pedrocarrillo.expensetracker.interfaces.IExpensesType;

import java.util.List;

/**
 * Created by pedrocarrillo on 3/20/16.
 */
public class HistoryFileParser implements FileGeneratorParser {

    @Override
    public String generateFileContent() {
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("Expense Tracker ").append(Util.formatDateToString(DateManager.getInstance().getDateFrom(), Util.getCurrentDateFormat())).append(" - ").append(Util.formatDateToString(DateManager.getInstance().getDateTo(), Util.getCurrentDateFormat())).append(addNextLine());
        List<Expense> expenseList = ExpensesManager.getInstance().getExpensesList();
        contentBuilder.append(addNextLine());
        for (Expense expense : expenseList) {
            contentBuilder.append(Util.formatDateToString(expense.getDate(), Util.getCurrentDateFormat())).append(addTab());
            String type;
            if (expense.getType() == IExpensesType.MODE_EXPENSES) {
                type = ExpenseTrackerApp.getContext().getResources().getString(R.string.expense);
            } else {
                type = ExpenseTrackerApp.getContext().getResources().getString(R.string.income);
            }
            contentBuilder.append(type).append(addTab());
            contentBuilder.append(expense.getCategory().getName()).append(addTab());
            contentBuilder.append(expense.getDescription()).append(addTab());
            contentBuilder.append(expense.getTotal()).append(addNextLine());

        }
        contentBuilder.append(addNextLine());
        float total = Expense.getCategoryTotalByDate(DateManager.getInstance().getDateFrom(), DateManager.getInstance().getDateTo(), null);
        contentBuilder.append("Total").append(addTab()).append(total);
        return contentBuilder.toString();
    }

    public static String addNextLine() {
        return "\n";
    }

    public static String addTab() {
        return "\t";
    }

}
