package com.pedrocarrillo.expensetracker.custom;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.pedrocarrillo.expensetracker.R;
import com.pedrocarrillo.expensetracker.interfaces.ISelectDateFragment;
import com.pedrocarrillo.expensetracker.utils.DateUtils;
import com.pedrocarrillo.expensetracker.utils.DialogManager;
import com.pedrocarrillo.expensetracker.utils.Util;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by pcarrillo on 13/10/2015.
 */
public class SelectDateFragment extends Fragment implements View.OnClickListener{

    private Button btnDateFrom;
    private Button btnDateTo;
    private TextView tvTotal;

    private ISelectDateFragment iSelectDateFragment;
    private Date mDateFrom;
    private Date mDateTo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_date_range, container, false);
        btnDateFrom = (Button)rootView.findViewById(R.id.btn_date_from);
        btnDateTo = (Button)rootView.findViewById(R.id.btn_date_to);
        tvTotal = (TextView)rootView.findViewById(R.id.tv_total);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btnDateFrom.setOnClickListener(this);
        btnDateTo.setOnClickListener(this);
        mDateFrom = DateUtils.getFirstDateOfCurrentMonth();
        mDateTo = DateUtils.getLastDateOfCurrentMonth();
        updateDate(btnDateFrom, mDateFrom);
        updateDate(btnDateTo, mDateTo);
        iSelectDateFragment.updateData();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_date_from || v.getId() == R.id.btn_date_to) {
            showDateDialog(v.getId());
        }
    }

    private void showDateDialog(final int id) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(id == R.id.btn_date_from ? mDateFrom : mDateTo);
        DialogManager.getInstance()
                .showDatePicker(
                        getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                calendar.set(year, month, day);
                                DateUtils.setDateStartOfDay(calendar);
                                if (id == R.id.btn_date_from) {
                                    mDateFrom = calendar.getTime();
                                    updateDate(btnDateFrom, mDateFrom);
                                } else {
                                    mDateTo = calendar.getTime();
                                    updateDate(btnDateTo, mDateTo);
                                }
                                iSelectDateFragment.updateData();
                            }
                        },
                        calendar,
                        (R.id.btn_date_from == id) ? null : mDateFrom,
                        (R.id.btn_date_from == id) ? mDateTo : null);
    }

    private void updateDate(Button btn, Date date) {
        btn.setText(Util.formatDateToString(date, "MM/dd/yyyy"));
    }

    public Date getDateFrom() {
        return mDateFrom;
    }

    public Date getDateTo() {
        return mDateTo;
    }

    public TextView getTextViewTotal() {
        return tvTotal;
    }

    public void setSelectDateFragment(ISelectDateFragment iSelectDateFragment) {
        this.iSelectDateFragment = iSelectDateFragment;
    }

}
