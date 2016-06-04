package com.pedrocarrillo.expensetracker.ui;

import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.entities.Category;
import com.pedrocarrillo.expensetracker.entities.Expense;
import com.pedrocarrillo.expensetracker.interfaces.IConstants;
import com.pedrocarrillo.expensetracker.interfaces.IDateMode;
import com.pedrocarrillo.expensetracker.interfaces.IExpensesMode;
import com.pedrocarrillo.expensetracker.interfaces.IExpensesType;
import com.pedrocarrillo.expensetracker.interfaces.IMainActivityListener;
import com.pedrocarrillo.expensetracker.ui.categories.CategoriesFragment;
import com.pedrocarrillo.expensetracker.ui.expenses.ExpensesContainerFragment;
import com.pedrocarrillo.expensetracker.ui.expenses.ExpensesFragment;
import com.pedrocarrillo.expensetracker.ui.expenses.ExpensesViewPagerAdapter;
import com.pedrocarrillo.expensetracker.ui.help.HelpActivity;
import com.pedrocarrillo.expensetracker.ui.history.HistoryFragment;
import com.pedrocarrillo.expensetracker.ui.reminders.ReminderFragment;
import com.pedrocarrillo.expensetracker.ui.settings.SettingsActivity;
import com.pedrocarrillo.expensetracker.ui.statistics.StatisticsFragment;
import com.pedrocarrillo.expensetracker.utils.BudgetManager;
import com.pedrocarrillo.expensetracker.utils.DateUtils;
import com.pedrocarrillo.expensetracker.utils.ExpensesManager;
import com.pedrocarrillo.expensetracker.utils.RealmManager;
import com.pedrocarrillo.expensetracker.utils.Util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, IMainActivityListener {

    @IntDef({NAVIGATION_MODE_STANDARD, NAVIGATION_MODE_TABS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NavigationMode {}

    public static final int NAVIGATION_MODE_STANDARD = 0;
    public static final int NAVIGATION_MODE_TABS = 1;
    public static final String NAVIGATION_POSITION = "navigation_position";

    private int mCurrentMode = NAVIGATION_MODE_STANDARD;
    private IExpensesMode currentExpensesMode;
    private int idSelectedNavigationItem;

    private DrawerLayout mainDrawerLayout;
    private NavigationView mainNavigationView;
    private Toolbar mToolbar;
    private TabLayout mainTabLayout;
    private FloatingActionButton mFloatingActionButton;


    // Expenses Summary related views
    private LinearLayout llExpensesSummary, llBudgetSummary;
    private TextView tvDate;
    private TextView tvDescription;
    private TextView tvTotal;

    //Budget Summary related views
    private TextView tvBudgetInfo;
    private TextView tvBudgetTotal;
    private TextView tvBudgetDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        setUpDrawer();
        setUpToolbar();
        if (savedInstanceState != null) {
            int menuItemId = savedInstanceState.getInt(NAVIGATION_POSITION);
            mainNavigationView.setCheckedItem(menuItemId);
            mainNavigationView.getMenu().performIdentifierAction(menuItemId, 0);
        } else {
            mainNavigationView.getMenu().performIdentifierAction(R.id.nav_expenses, 0);
        }
    }

    @NavigationMode
    public int getNavigationMode() {
        return mCurrentMode;
    }

    private void initUI() {
        mainDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mainTabLayout = (TabLayout)findViewById(R.id.tab_layout);
        mainNavigationView = (NavigationView)findViewById(R.id.nav_view);
        mFloatingActionButton = (FloatingActionButton)findViewById(R.id.fab_main);
        llExpensesSummary = (LinearLayout)findViewById(R.id.ll_expense_container);
        llBudgetSummary = (LinearLayout)findViewById(R.id.ll_budget_container);
        tvDate = (TextView)findViewById(R.id.tv_date);
        tvDescription = (TextView)findViewById(R.id.tv_description);
        tvTotal = (TextView)findViewById(R.id.tv_total);
        tvBudgetInfo = (TextView)findViewById(R.id.tv_budget_info);
        tvBudgetDescription = (TextView)findViewById(R.id.tv_budget_description);
        tvBudgetTotal = (TextView)findViewById(R.id.tv_budget_total);

        if (Category.getCategoriesExpense().isEmpty()) {
            Category foodCategory = new Category(getString(R.string.food_category), IExpensesType.MODE_EXPENSES);
            Category transportCategory = new Category(getString(R.string.transportation_category), IExpensesType.MODE_EXPENSES);
            Category otherCategory = new Category(getString(R.string.other_category), IExpensesType.MODE_EXPENSES);
            RealmManager.getInstance().save(foodCategory, Category.class);
            RealmManager.getInstance().save(transportCategory, Category.class);
            RealmManager.getInstance().save(otherCategory, Category.class);
        }
    }

    private void setUpDrawer() {
        mainNavigationView.setNavigationItemSelectedListener(this);
    }

    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_help) {
            startActivity(new Intent(this, HelpActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(NAVIGATION_POSITION, idSelectedNavigationItem);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        menuItem.setChecked(true);
        mainDrawerLayout.closeDrawers();
        switchFragment(menuItem.getItemId());
        return false;
    }

    @Override
    public void setTabs(List<String> tabList, final TabLayout.OnTabSelectedListener onTabSelectedListener) {
        mainTabLayout.removeAllTabs();
        mainTabLayout.setVisibility(View.VISIBLE);
        mainTabLayout.setOnTabSelectedListener(onTabSelectedListener);
        for (String tab : tabList) {
            mainTabLayout.addTab(mainTabLayout.newTab().setText(tab).setTag(tab));
        }
    }

    @Override
    public void setMode(@NavigationMode int mode) {
        mFloatingActionButton.setVisibility(View.GONE);
        llExpensesSummary.setVisibility(View.GONE);
        llBudgetSummary.setVisibility(View.GONE);
        mCurrentMode = mode;
        switch (mode) {
            case NAVIGATION_MODE_STANDARD:
                setNavigationModeStandard();
                break;
            case NAVIGATION_MODE_TABS:
                setNavigationModeTabs();
                break;
        }
    }

    @Override
    public void setExpensesSummary(@IDateMode int dateMode) {
        if (currentExpensesMode == IExpensesMode.EXPENSES) {
            llExpensesSummary.setVisibility(View.VISIBLE);
            llBudgetSummary.setVisibility(View.GONE);
            float total = Expense.getTotalExpensesByDateMode(dateMode);
            int resourceColor = Expense.getExpensesTotalIntColorResource(dateMode);
            StringBuilder totalString = new StringBuilder();
            if (resourceColor == R.color.colorAccentGreen) {
                totalString.append("+ ");
            } else if (resourceColor == R.color.colorAccentRed) {
                totalString.append("- ");
            }
            totalString.append(Util.getFormattedCurrency(total));
            tvTotal.setText(totalString.toString());
            String date;
            switch (dateMode) {
                case IDateMode.MODE_TODAY:
                    date = Util.formatDateToString(DateUtils.getToday(), Util.getCurrentDateFormat());
                    break;
                case IDateMode.MODE_WEEK:
                    date = Util.formatDateToString(DateUtils.getFirstDateOfCurrentWeek(), Util.getCurrentDateFormat()).concat(" - ").concat(Util.formatDateToString(DateUtils.getRealLastDateOfCurrentWeek(), Util.getCurrentDateFormat()));
                    break;
                case IDateMode.MODE_MONTH:
                    date = Util.formatDateToString(DateUtils.getFirstDateOfCurrentMonth(), Util.getCurrentDateFormat()).concat(" - ").concat(Util.formatDateToString(DateUtils.getRealLastDateOfCurrentMonth(), Util.getCurrentDateFormat()));
                    break;
                default:
                    date = "";
                    break;
            }
            tvDate.setText(date);
        } else {
            llExpensesSummary.setVisibility(View.GONE);
            llBudgetSummary.setVisibility(View.VISIBLE);
            float total = Expense.getTotalExpensesByDateMode(dateMode);
            int resourceColor = Expense.getExpensesTotalIntColorResource(dateMode);
            float availableTotal;
            float budgetTotal;
            String dateModeString;
            switch (dateMode) {
                case IDateMode.MODE_TODAY:
                    dateModeString = getString(R.string.today);
                    budgetTotal = BudgetManager.getInstance().getDailyAvailableBudget();
                    availableTotal = BudgetManager.getInstance().getDailyAvailableBudget() - total;
                    break;
                case IDateMode.MODE_WEEK:
                    dateModeString = getString(R.string.this_week);
                    budgetTotal = BudgetManager.getInstance().getWeeklyAvailableBudget();
                    availableTotal = BudgetManager.getInstance().getWeeklyAvailableBudget() - total;
                    break;
                case IDateMode.MODE_MONTH:
                    dateModeString = getString(R.string.this_month);
                    budgetTotal = BudgetManager.getInstance().getMonthlyAvailableBudget();
                    availableTotal = BudgetManager.getInstance().getMonthlyAvailableBudget() - total;
                    break;
                default:
                    availableTotal = 0;
                    dateModeString = "";
                    budgetTotal = 0;
            }
            tvBudgetDescription.setText(getString(R.string.you_have_available, dateModeString));
            tvBudgetInfo.setText(getString(R.string.budget_goal_info, String.valueOf(budgetTotal), String.valueOf(BudgetManager.getInstance().getIntendedSavings())));
            tvBudgetTotal.setText(String.valueOf(availableTotal));
        }
    }

    @Override
    public void setExpenseMode(IExpensesMode expenseMode) {
        currentExpensesMode = expenseMode;
    }

    @Override
    public void setFAB(@DrawableRes int drawableId, View.OnClickListener onClickListener) {
        mFloatingActionButton.setImageDrawable(getResources().getDrawable(drawableId));
        mFloatingActionButton.setOnClickListener(onClickListener);
        mFloatingActionButton.show();
    }

    @Override
    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void setPager(ViewPager vp, final TabLayout.ViewPagerOnTabSelectedListener viewPagerOnTabSelectedListener) {
        mainTabLayout.setupWithViewPager(vp);
        mainTabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(vp) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                @IDateMode int dateMode;
                switch (tab.getPosition()) {
                    case 0:
                        dateMode = IDateMode.MODE_TODAY;
                        break;
                    case 1:
                        dateMode = IDateMode.MODE_WEEK;
                        break;
                    case 2:
                        dateMode = IDateMode.MODE_MONTH;
                        break;
                    default:
                        dateMode = IDateMode.MODE_TODAY;
                }
                setExpensesSummary(dateMode);
                viewPagerOnTabSelectedListener.onTabSelected(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                viewPagerOnTabSelectedListener.onTabUnselected(tab);
            }
        });
        setExpensesSummary(IDateMode.MODE_TODAY);
    }

    public ActionMode setActionMode(final ActionMode.Callback actionModeCallback) {
       return mToolbar.startActionMode(new ActionMode.Callback() {
           @Override
           public boolean onCreateActionMode(ActionMode mode, Menu menu) {
               mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
               return actionModeCallback.onCreateActionMode(mode,menu);
           }

           @Override
           public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
               return actionModeCallback.onPrepareActionMode(mode, menu);
           }

           @Override
           public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
               return actionModeCallback.onActionItemClicked(mode, item);
           }

           @Override
           public void onDestroyActionMode(ActionMode mode) {
               mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
               actionModeCallback.onDestroyActionMode(mode);
           }
       });
    }

    private void setNavigationModeTabs() {
        mainTabLayout.setVisibility(View.VISIBLE);
    }

    private void setNavigationModeStandard() {
        CoordinatorLayout coordinator = (CoordinatorLayout) findViewById(R.id.main_coordinator);
        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.app_bar_layout);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appbar.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if (behavior != null && appbar != null) {
            int[] consumed = new int[2];
            behavior.onNestedPreScroll(coordinator, appbar, null, 0, -1000, consumed);
        }
        mainTabLayout.setVisibility(View.GONE);
    }

    private void switchFragment(int menuItemId) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_content);
        switch (menuItemId) {
            case R.id.nav_expenses:
                replaceFragment(ExpensesContainerFragment.newInstance(IExpensesMode.EXPENSES), false);
                break;
            case R.id.nav_budget:
                replaceFragment(ExpensesContainerFragment.newInstance(IExpensesMode.BUDGET), false);
                break;
            case R.id.nav_categories:
                if (!(currentFragment instanceof  CategoriesFragment)) replaceFragment(CategoriesFragment.newInstance(), false);
                break;
            case R.id.nav_statistics:
                if (!(currentFragment instanceof  StatisticsFragment)) replaceFragment(StatisticsFragment.newInstance(), false);
                break;
            case R.id.nav_reminders:
                if (!(currentFragment instanceof  ReminderFragment)) replaceFragment(ReminderFragment.newInstance(), false);
                break;
            case R.id.nav_history:
                if (!(currentFragment instanceof HistoryFragment)) replaceFragment(HistoryFragment.newInstance(), false);
                break;
        }
    }
}
