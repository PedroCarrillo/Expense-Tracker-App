package com.pedrocarrillo.expensetracker.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
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

import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.interfaces.IDateMode;
import com.pedrocarrillo.expensetracker.interfaces.IMainActivityListener;
import com.pedrocarrillo.expensetracker.ui.categories.CategoriesFragment;
import com.pedrocarrillo.expensetracker.ui.expenses.ExpensesContainerFragment;
import com.pedrocarrillo.expensetracker.ui.expenses.ExpensesFragment;
import com.pedrocarrillo.expensetracker.ui.expenses.ExpensesViewPagerAdapter;
import com.pedrocarrillo.expensetracker.ui.reminders.ReminderFragment;
import com.pedrocarrillo.expensetracker.ui.settings.SettingsActivity;
import com.pedrocarrillo.expensetracker.ui.statistics.StatisticsFragment;

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
    private int idSelectedNavigationItem;

    private DrawerLayout mainDrawerLayout;
    private NavigationView mainNavigationView;
    private Toolbar mToolbar;
    private TabLayout mainTabLayout;
    private FloatingActionButton mFloatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        setUpDrawer();
        setUpToolbar();
        if ( savedInstanceState != null) {
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
        idSelectedNavigationItem = menuItem.getItemId();
        switchFragment(menuItem.getItemId());
        return false;
    }

    @Override
    public void setTabs(List<String> tabList, TabLayout.OnTabSelectedListener onTabSelectedListener) {
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
    public void setPager(ViewPager vp) {
        mainTabLayout.setupWithViewPager(vp);
    }

    public ActionMode setActionMode(ActionMode.Callback actionModeCallback) {
       return mToolbar.startActionMode(actionModeCallback);
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
                if (!(currentFragment instanceof ExpensesContainerFragment)) replaceFragment(ExpensesContainerFragment.newInstance(), false);
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
        }
    }
}
