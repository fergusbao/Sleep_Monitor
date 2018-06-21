package edu.njit.junyi.sleep_monitor_v1.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;

import edu.njit.junyi.sleep_monitor_v1.R;
import edu.njit.junyi.sleep_monitor_v1.model.MyDate;
import edu.njit.junyi.sleep_monitor_v1.model.Point;
import edu.njit.junyi.sleep_monitor_v1.storage.DataItem;
import edu.njit.junyi.sleep_monitor_v1.storage.JSONHelper;

/**
 * Created by junyi on 4/4/18.
 */

public class DetailActivity extends AppCompatActivity {
    private Typeface font;
    private String fileName;

    private long startTime;
    private long endTime;
    private DataItem dataItem;
    private List<Long> snore;
    private List<Long> movement;
    private BarChart snoreBarChart;
    private BarChart movementBarChart;
    private PieChart pieChart;
    private String TAG = "test";
    private List<Point> snorePoints;
    private List<Point> movementPoints;
    private float scalePercent = 2f / 32f;
    private String[] values = new String[]
            {"0hr", "0.25", "0.5", "0.75", "1hr", "1.25", "1.5", "1.75", "2hr",
                    "2.25", "2.5", "2.75", "3hr", "3.25", "3.5", "3.75", "4hr",
                    "4.25", "4.5", "4.75", "5hr", "5.25", "5.5", "5.75", "6hr",
                    "6.25", "6.5", "6.75", "7hr", "7.25", "7.5", "7.75", "8hr",
                    "8.25", "8.5", "8.75", "9hr", "9.25", "9.5", "9.75", "10hr"};
    private double sleepDuration = 0d;
    private double deepSleepDuration = 0d;

    class MyXAxisValueFormatter implements IAxisValueFormatter {

        private String[] mValues;

        public MyXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            return mValues[(int) value];
        }

        /**
         * this is only needed if numbers are returned, else return 0
         */
        public int getDecimalDigits() {
            return 0;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        font = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold.ttf");

        TextView textView1 = findViewById(R.id.textView1);
        TextView textView2 = findViewById(R.id.textView2);
        snoreBarChart = findViewById(R.id.snoreBarChart);
        movementBarChart = findViewById(R.id.movementBarChart);
        pieChart = findViewById(R.id.pieChart);
        snorePoints = new ArrayList<>();
        movementPoints = new ArrayList<>();

        Intent intent = getIntent();
        fileName = intent.getStringExtra("nv_name");
//        textView1.setText(fileName);
        Bundle bundleExtra = intent.getBundleExtra("bundle");
//        textView2.setText(bundleExtra.getString("nan_name"));

        getData();
        analyser();
        Log.i(TAG, "snore: " + snore.toString());
        Log.i(TAG, "snore size: " + snore.size());
        Log.i(TAG, "movement: " + movement.toString());
        Log.i(TAG, "start time: " + startTime);
        Log.i(TAG, "end time: " + endTime);
        count();
        for (Point point : movementPoints) {
            Log.i(TAG, "output: " + point);
        }
        drawSnore();
        drawMovement();
        setChartData(snorePoints.size(), movementPoints.size());
        drawPieChart();
    }

    private void setChartData(int num1, int num2) {
        Matrix matrix1 = new Matrix();
        matrix1.postScale(scaleNum(num1), 1f);
        Matrix matrix2 = new Matrix();
        matrix2.postScale(scaleNum(num2), 1f);
        snoreBarChart.getViewPortHandler().refresh(matrix1, snoreBarChart, false);
        movementBarChart.getViewPortHandler().refresh(matrix2, movementBarChart, false);
    }

    private double sleepDurationHour(long mStart, long mEnd) {
        MyDate start = new MyDate(mStart);
        MyDate end = new MyDate(mEnd);
        double sleepDuration = (double) (end.getDay() - start.getDay()) * 24d
                + (double) (end.getHour() - start.getHour())
                + (double) (end.getMinute() - start.getMinute()) / 60d
                + (double) (end.getSecond() - start.getSecond()) / 3600d;
        return sleepDuration;
    }

    private String getSleepQuality() {
        sleepDuration = sleepDurationHour(startTime, endTime);
        if (sleepDuration < 6 && sleepDuration > 10) {
            return "Bad";
        } else {
            for (int i = 0; i < movement.size() - 1; i++) {
                double temp = betweenNum1AndNum2(movement.get(i), movement.get(i + 1));
                if (temp != 0) {
                    if (checkSnore(movement.get(i), movement.get(i + 1))) {
                        deepSleepDuration += temp;
                    }
                }
            }
            if (deepSleepDuration < 67.5) {
                return "Bad";
            } else if (deepSleepDuration <= 90) {
                return "Good";
            } else {
                return "Excellent";
            }
        }
    }

    private boolean checkSnore(long time1, long time2) {
        for (long num : snore) {
            if (num >= time1 && num <= time2) {
                return true;
            }
        }
        return false;
    }

    private double betweenNum1AndNum2(long time1, long time2) {
        double min = 13.5d;
        double max = 18d;
        double durationMinute = sleepDurationHour(time1, time2) * 60d;
        if (durationMinute < min) {
            return 0d;
        } else if (durationMinute >= min && durationMinute <= max) {
            return durationMinute;
        } else {
            return max;
        }
    }

    private float scaleNum(int xCount) {
        return xCount * scalePercent;
    }

    public void drawPieChart() {
        pieChart.getDescription().setEnabled(false);
        pieChart.setBackgroundColor(ColorTemplate.rgb("#EEEEEE"));
        pieChart.setUsePercentValues(true);
        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setCenterTextTypeface(font);
        pieChart.setEntryLabelTypeface(font);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());


        List<PieEntry> entries = new ArrayList<>();

        String result = getSleepQuality();
        float deep = (float) deepSleepDuration;
        float deepPercent = (int) (deep / (float) sleepDuration / 60f * 100f * 10) / 10f;
        float light = (float) sleepDuration * 60 - deep;
        float lightPercent = (int) (light / (float) sleepDuration / 60f * 100f * 10) / 10f;
        entries.add(new PieEntry(lightPercent, "Light sleep"));
        entries.add(new PieEntry(deepPercent, "Deep sleep"));
//        entries.add(new PieEntry(10.5f, "Others"));
//        entries.add(new PieEntry(30.8f, "Blue"));

        PieDataSet set = new PieDataSet(entries, "");
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = 5 * (metrics.densityDpi / 160f);
        set.setSelectionShift(px);
        set.setValueTextSize(12);
        set.setColors(colors);
        PieData data = new PieData(set);
        data.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                return entry.getY() + "%";
            }
        });

        pieChart.setCenterText(result);
        pieChart.setCenterTextSize(20);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setHoleColor(ColorTemplate.rgb("#FF4081"));
        pieChart.setCenterTextColor(ColorTemplate.rgb("#303F9F"));
        pieChart.setData(data);
        pieChart.invalidate(); // refresh
        pieChart.animateX(2200);
    }

    public void count() {
        MyDate start = new MyDate(startTime);
        MyDate end = new MyDate(endTime);
        MyDate nextStart = getNextTime(start, end);
        int i = 1;
        int count = 0;

        if (snore.size() == 0) {
            do {
                snorePoints.add(new Point(i, 0));
                start = nextStart;
                nextStart = getNextTime(start, end);
                i++;
            }
            while (nextStart != end);
        } else {
            do {
                for (Long num : snore) {
                    if (inBetween(num, start, nextStart)) {
                        count++;
                    }
                }
                snorePoints.add(new Point(i, count));
                start = nextStart;
                nextStart = getNextTime(start, end);
                count = 0;
                i++;
            }
            while (nextStart != end);
        }


        i = 1;
        count = 0;
        start = new MyDate(startTime);
        nextStart = getNextTime(start, end);
        if (movement.size() == 0) {
            do {
                movementPoints.add(new Point(i, 0));
                start = nextStart;
                nextStart = getNextTime(start, end);
                i++;
            }
            while (nextStart != end);
        } else {
            do {
                for (Long num : movement) {
                    if (inBetween(num, start, nextStart)) {
                        count++;
                    }
                }
                movementPoints.add(new Point(i, count));
                start = nextStart;
                nextStart = getNextTime(start, end);
                count = 0;
                i++;
            } while (nextStart != end);
        }
    }

    public boolean inBetween(Long time, MyDate start, MyDate end) {
        MyDate temp = new MyDate(time);
        if (getBetween(start, temp) >= 0 && getBetween(temp, end) > 0) {
            return true;
        } else {
            return false;
        }
    }

    public long getBetween(MyDate first, MyDate second) {
        long result = 0l;
        result += (second.getDay() - first.getDay()) * 24 * 60 * 60;
        result += getSeconds(second) - getSeconds(first);
        return result;
    }

    public long getSeconds(MyDate myDate) {
        long result = 0l;
        result += myDate.getSecond();
        result += myDate.getMinute() * 60;
        result += myDate.getHour() * 60 * 60;
        return result;
    }

    public MyDate getNextTime(MyDate start, MyDate end) {
        if (getBetween(start, end) <= 0) {
            return end;
        }

        MyDate temp = new MyDate(start.getDate());
        ;
        if (temp.getMinute() + 15 >= 60) {
            temp.setMinute(temp.getMinute() + 15 - 60);
            if (temp.getHour() + 1 >= 24) {
                temp.setHour(temp.getHour() + 1 - 24);
                temp.setDay(temp.getDay() + 1);
            } else {
                temp.setHour(temp.getHour() + 1);
            }
        } else {
            temp.setMinute(temp.getMinute() + 15);
        }
        temp.setDate(convertDate(temp));
        return temp;
    }

    public Long convertDate(MyDate myDate) {
        String str = "";
        str += Integer.toString(myDate.getYear())
                + (myDate.getMonth() < 10 ?
                "0" + Integer.toString(myDate.getMonth())
                : Integer.toString(myDate.getMonth()))
                + (myDate.getDay() < 10 ?
                "0" + Integer.toString(myDate.getDay())
                : Integer.toString(myDate.getDay()))
                + (myDate.getHour() < 10 ?
                "0" + Integer.toString(myDate.getHour())
                : Integer.toString(myDate.getHour()))
                + (myDate.getMinute() < 10 ?
                "0" + Integer.toString(myDate.getMinute())
                : Integer.toString(myDate.getMinute()))
                + (myDate.getSecond() < 10 ?
                "0" + Integer.toString(myDate.getSecond())
                : Integer.toString(myDate.getSecond()));

        return Long.parseLong(str);
    }

    public void getData() {
        dataItem = JSONHelper.importFromJSON(fileName);
        snore = dataItem.getSnore();
        movement = dataItem.getMovement();
        startTime = dataItem.getStartTime();
        endTime = dataItem.getEndTime();
    }

    public void drawSnore() {
        snoreBarChart.getDescription().setEnabled(false);
        snoreBarChart.setBackgroundColor(ColorTemplate.rgb("#EEEEEE"));

        List<BarEntry> entries = new ArrayList<>();
        for (Point point : snorePoints) {
            entries.add(new BarEntry(point.getValueX(), point.getValueY()));
        }

        BarDataSet set = new BarDataSet(entries, "Snore");
        set.setValueTextSize(9);
        set.setColor(ColorTemplate.rgb("#FF4081"));
        BarData data = new BarData(set);
        data.setBarWidth(0.9f); // set custom bar width
        snoreBarChart.setData(data);
        snoreBarChart.setFitBars(true); // make the x-axis fit exactly all bars
        snoreBarChart.invalidate(); // refresh

        data.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                if (entry.getY() == 0f) {
                    return "";
                } else {
                    return Float.toString(entry.getY()).replace(".0", "");
                }
            }
        });

        XAxis xAxis = snoreBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(font);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(15);

        YAxis leftAxis = snoreBarChart.getAxisLeft();
        leftAxis.setTypeface(font);
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = snoreBarChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(font);
        rightAxis.setLabelCount(8, false);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        Legend l = snoreBarChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);

        xAxis.setValueFormatter(new MyXAxisValueFormatter(values));

        snoreBarChart.animateX(4000);
        // set a custom value formatter
        //        xAxis.setValueFormatter(new MyXAxisValueFormatter());
        // and more...
    }

    public void drawMovement() {
        movementBarChart.getDescription().setEnabled(false);
        movementBarChart.setBackgroundColor(ColorTemplate.rgb("#EEEEEE"));
        List<BarEntry> entries = new ArrayList<>();
        for (Point point : movementPoints) {
            entries.add(new BarEntry((int) point.getValueX(), (int) point.getValueY()));
        }

        BarDataSet set = new BarDataSet(entries, "Movement");
        set.setValueTextSize(9);
        set.setColor(ColorTemplate.rgb("#3F51B5"));
        BarData data = new BarData(set);
        data.setBarWidth(0.9f); // set custom bar width

        movementBarChart.setData(data);
        movementBarChart.setFitBars(true); // make the x-axis fit exactly all bars
        movementBarChart.invalidate(); // refresh

        data.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                if (entry.getY() == 0f) {
                    return "";
                } else {
                    return Float.toString(entry.getY()).replace(".0", "");
                }
            }
        });

        XAxis xAxis = movementBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(font);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(15);

        YAxis leftAxis = movementBarChart.getAxisLeft();
        leftAxis.setTypeface(font);
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = movementBarChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(font);
        rightAxis.setLabelCount(8, false);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        Legend l = movementBarChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);

        xAxis.setValueFormatter(new MyXAxisValueFormatter(values));

        movementBarChart.animateX(4000);
        // set a custom value formatter
        //        xAxis.setValueFormatter(new MyXAxisValueFormatter());
        // and more...
    }


    public void analyser() {
        List<Long> temp = new ArrayList<>();
        long start;
        if (snore.size() != 0) {
            start = snore.get(0);
            temp.add(start);
            for (Long num : snore) {
                if (checker1Minute(start, num)) {
                    temp.add(num);
                    start = num;
                }
            }
            snore = temp;
        }
        if (movement.size() != 0) {
            start = startTime;
            temp = new ArrayList<>();
            start = movement.get(0);
            temp.add(start);
            for (Long num : movement) {
                if (checker1Minute(start, num)) {
                    temp.add(num);
                    start = num;
                }
            }
            movement = temp;
        }
    }

    public boolean checker1Minute(long start, long end) {
        long temp = end - start;
        if (temp > 60) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checker15Minute(long start, long end) {
        long temp = end - start;
        if (temp > 1_500_000) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.quit, menu);
        menu.add(1, Menu.FIRST, 1, "Menu");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.quit:
                finish();
                System.exit(0);
                return true;
            case 1:
                startActivity(new Intent(DetailActivity.this, MenuActivity.class));
                finish();
            default:
                return false;
        }
    }
}

