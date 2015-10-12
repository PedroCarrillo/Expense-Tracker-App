package com.pedrocarrillo.expensetracker.entities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.pedrocarrillo.expensetracker.ExpenseTrackerApp;
import com.pedrocarrillo.expensetracker.receivers.AlarmReceiver;
import com.pedrocarrillo.expensetracker.ui.reminders.NewReminderFragment;
import com.pedrocarrillo.expensetracker.utils.DateUtils;
import com.pedrocarrillo.expensetracker.utils.RealmManager;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Pedro on 9/25/2015.
 */
public class Reminder extends RealmObject {

    @PrimaryKey
    private String id;

    private String name;
    private boolean state;
    private int day;
    private Date date;
    private Date createdAt;

    public Reminder() {}

    public Reminder(String name, int day, boolean state, Date date) {
        this.name = name;
        this.day = day;
        this.state = state;
        this.date = date;
        this.createdAt = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public static List<Reminder> getReminders() {
        return RealmManager.getInstance().getRealmInstance().where(Reminder.class).findAll();
    }

    public static void saveNewReminder(String name, int daySelected, boolean state, Date timeSelected) {
        Reminder reminder = new Reminder(name, daySelected, true, timeSelected);
        RealmManager.getInstance().save(reminder, Reminder.class);
        setReminder(reminder);
    }

    public static void setReminder(Reminder reminder) {
        Calendar alarmCalendar = Calendar.getInstance();
        Calendar reminderDate = Calendar.getInstance();
        reminderDate.setTime(reminder.getDate());
        if (reminder.getDay() <= alarmCalendar.get(Calendar.DAY_OF_MONTH) && !DateUtils.isToday(reminder.getCreatedAt())) {
            alarmCalendar.setTime(DateUtils.getLastDateOfCurrentMonth());
        }
        alarmCalendar.set(Calendar.DATE, reminder.getDay());
        alarmCalendar.set(Calendar.HOUR_OF_DAY, reminderDate.get(Calendar.HOUR_OF_DAY));
        alarmCalendar.set(Calendar.MINUTE, reminderDate.get(Calendar.MINUTE));

        AlarmManager alarmMgr = (AlarmManager) ExpenseTrackerApp.getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ExpenseTrackerApp.getContext(), AlarmReceiver.class);
        intent.putExtra(NewReminderFragment.REMINDER_ID_KEY, reminder.getId());

        PendingIntent alarmIntent = PendingIntent.getBroadcast(ExpenseTrackerApp.getContext(), (int) reminder.getCreatedAt().getTime(), intent, 0);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), alarmIntent);

    }

    public static void cancelReminder(Reminder reminder) {
        AlarmManager alarmMgr = (AlarmManager) ExpenseTrackerApp.getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ExpenseTrackerApp.getContext(), AlarmReceiver.class);
        intent.putExtra(NewReminderFragment.REMINDER_ID_KEY, reminder.getId());
        PendingIntent alarmIntent = PendingIntent.getBroadcast(ExpenseTrackerApp.getContext(), (int) reminder.getCreatedAt().getTime(), intent, 0);
        alarmMgr.cancel(alarmIntent);
    }

    public static void updateReminder(Reminder reminder, boolean checked) {
        Reminder reminderToUpdate = new Reminder(reminder.getName(), reminder.getDay(), checked, reminder.getDate());
        reminderToUpdate.setCreatedAt(reminder.getCreatedAt());
        reminderToUpdate.setId(reminder.getId());
        RealmManager.getInstance().update(reminderToUpdate);
        if (checked) {
            setReminder(reminder);
        } else {
            cancelReminder(reminder);
        }
    }

    public static void eraseReminder(Reminder reminder) {
        if (reminder.isState()) {
            cancelReminder(reminder);
        }
        RealmManager.getInstance().delete(reminder);
    }

    public static void eraseReminders(List<Reminder> reminderList) {
        for (Reminder reminder : reminderList) {
            if (reminder.isState()) {
                cancelReminder(reminder);
            }
        }
        RealmManager.getInstance().delete(reminderList);
    }
}