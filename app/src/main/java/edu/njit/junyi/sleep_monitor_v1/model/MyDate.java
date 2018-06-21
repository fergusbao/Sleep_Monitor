package edu.njit.junyi.sleep_monitor_v1.model;

/**
 * Created by junyi on 4/17/18.
 */

public class MyDate {
    private long date;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;

    public MyDate(long date) {
        this.date = date;
        analyse();
    }

    private void analyse() {
        long temp = date;
        setSecond((int) (temp % 100));
        temp /= 100;
        setMinute((int) (temp % 100));
        temp /= 100;
        setHour((int) (temp % 100));
        temp /= 100;
        setDay((int) (temp % 100));
        temp /= 100;
        setMonth((int) (temp % 100));
        temp /= 100;
        setYear((int) temp);
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    @Override
    public String toString() {
        return "MyDate{" +
                "date=" + date +
                ", year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", hour=" + hour +
                ", minute=" + minute +
                ", second=" + second +
                '}';
    }
}
