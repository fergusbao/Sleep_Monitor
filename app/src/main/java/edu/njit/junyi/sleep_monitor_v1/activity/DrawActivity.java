package edu.njit.junyi.sleep_monitor_v1.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import edu.njit.junyi.sleep_monitor_v1.R;
import edu.njit.junyi.sleep_monitor_v1.model.SoundModel;
import edu.njit.junyi.sleep_monitor_v1.services.AudioRecordService;
import edu.njit.junyi.sleep_monitor_v1.storage.DataItem;
import edu.njit.junyi.sleep_monitor_v1.storage.JSONHelper;

/**
 * Created by junyi on 3/20/18.
 */

public class DrawActivity extends AppCompatActivity {

    private Typeface font;

    private TextView rlh;
    private TextView rms;
    private TextView var;
    private TextView snore;
    private TextView movement;

    private SoundModel soundModel;
    private AudioRecordService audioRecordService;
    private int snoreCount = 0;
    private int movementCount = 0;
    DataItem dataItem;
    String fileName;
    public final static String FILE_Name = "file name";

    private List<Integer> lists = new ArrayList<>();

    private LineChart lineChart;
    int[] mColors = ColorTemplate.VORDIPLOM_COLORS;
    private Random random;
    private LineData lineData;
    ArrayList<ILineDataSet> dataSets;

    private LineDataSet set;
    private float n = 1f;


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            soundModel = (SoundModel) intent.getSerializableExtra(AudioRecordService.MESSAGE_KEY);
            float msgRLH = ((int) (soundModel.getRLH() * 10)) / 10f;
            float msgRMS = ((int) (soundModel.getRMS() * 10)) / 10f;
            float msgVAR = ((int) (soundModel.getNormalizedVAR() * 100)) / 100f;
            rlh.setText(Float.toString(msgRLH));
            rms.setText(Float.toString(msgRMS));
            var.setText(Float.toString(msgVAR));

            if (lineData.getDataSetByIndex(0).getEntryCount() > 15) {
                lineData.getDataSetByIndex(0).removeEntry(0);

            }
            lineData.addEntry(new Entry(n, msgRLH), 0);

            if (lineData.getDataSetByIndex(1).getEntryCount() > 15) {
                lineData.getDataSetByIndex(1).removeEntry(0);
            }
            lineData.addEntry(new Entry(n, msgRMS), 1);

            if (lineData.getDataSetByIndex(2).getEntryCount() > 15) {
                lineData.getDataSetByIndex(2).removeEntry(0);
            }
            lineData.addEntry(new Entry(n, msgVAR), 2);
            Log.i("ddd", "VAR: " + msgVAR);

            n++;
            lineChart.notifyDataSetChanged(); // let the chart know it's data changed
            lineChart.invalidate(); // refresh

            snore.setText(Integer.toString(soundModel.getSnoreCount()));
            movement.setText(Integer.toString(soundModel.getMovementCount()));
            update();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        font = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Bold.ttf");

        Long currentTime;
        //soundModel = new SoundModel();
        audioRecordService = new AudioRecordService(soundModel);
        random = new Random();


        rlh = findViewById(R.id.rlh);
        rms = findViewById(R.id.rms);
        var = findViewById(R.id.var);
        snore = findViewById(R.id.snore);
        movement = findViewById(R.id.movement);
        audioRecordService.start();

        currentTime = getCurrentTime();
        fileName = currentTime.toString() + ".json";
        JSONHelper.createFolder();
        dataItem = new DataItem(currentTime);
        JSONHelper.exportToJSON(fileName, dataItem);
        dataItem = null;

        action();
        lineChart = findViewById(R.id.lineChart);
        drawLineChart();
    }


    private void removeDataSet() {

        LineData data = lineChart.getData();

        if (data != null) {

            data.removeDataSet(data.getDataSetByIndex(data.getDataSetCount() - 1));

            lineChart.notifyDataSetChanged();
            lineChart.invalidate();
        }
    }

    private void removeLastEntry() {

        LineData data = lineChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);

            if (set != null) {

                Entry e = set.getEntryForXValue(set.getEntryCount() - 1, Float.NaN);

                data.removeEntry(e, 0);
                // or remove by index
                // mData.removeEntryByXValue(xIndex, dataSetIndex);
                data.notifyDataChanged();
                lineChart.notifyDataSetChanged();
                lineChart.invalidate();
            }
        }
    }

    private void addDataSet() {

        LineData data = lineChart.getData();

        if (data != null) {

            int count = (data.getDataSetCount() + 1);

            ArrayList<Entry> yVals = new ArrayList<Entry>();

            for (int i = 0; i < data.getEntryCount(); i++) {
                yVals.add(new Entry(i, (float) (Math.random() * 50f) + 50f * count));
            }

            set = new LineDataSet(yVals, "DataSet " + count);
            set.setLineWidth(2.5f);
            set.setCircleRadius(4.5f);

            int color = mColors[count % mColors.length];

            set.setColor(color);
            set.setCircleColor(color);
            set.setHighLightColor(color);
            set.setValueTextSize(10f);
            set.setValueTextColor(color);

            data.addDataSet(set);
            data.notifyDataChanged();
            lineChart.notifyDataSetChanged();
            lineChart.invalidate();

        }
    }

    public void drawLineChart() {
        lineChart.getDescription().setEnabled(false);
        List<Entry> entries1 = new ArrayList<>();
        List<Entry> entries2 = new ArrayList<>();
        List<Entry> entries3 = new ArrayList<>();

        entries1.add(new Entry(0f, 0f));
        entries2.add(new Entry(0f, 0f));
        entries3.add(new Entry(0f, 0f));

        LineDataSet dataSetRLH = new LineDataSet(entries1, "RLH"); // add entries to dataset
        dataSetRLH.setColor(Color.RED);
        dataSetRLH.setLineWidth(1.5f);
        dataSetRLH.setValueTextColor(Color.BLACK); // styling, ...

        LineDataSet dataSetRMS = new LineDataSet(entries2, "RMS"); // add entries to dataset
        dataSetRMS.setColor(Color.GREEN);
        dataSetRMS.setLineWidth(1.5f);
        dataSetRMS.setValueTextColor(Color.BLACK); // styling, ...

        LineDataSet dataSetVAR = new LineDataSet(entries3, "VAR"); // add entries to dataset
        dataSetVAR.setColor(Color.BLUE);
        dataSetVAR.setLineWidth(1.5f);
        dataSetVAR.setValueTextColor(Color.BLACK); // styling, ...

        dataSets = new ArrayList<>();
        dataSets.add(dataSetRLH);
        dataSets.add(dataSetRMS);
        dataSets.add(dataSetVAR);


        lineData = new LineData(dataSets);
        lineData.setValueTextSize(11);
        lineData.setValueTypeface(font);
        lineChart.setData(lineData);
        lineChart.setAutoScaleMinMaxEnabled(true);

        lineChart.invalidate(); // refresh

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(font);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTypeface(font);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setLabelCount(15, false);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(-10f); // this replaces setStartAtZero(true)
        leftAxis.setAxisMaximum(200f);

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(font);
        rightAxis.setLabelCount(15, false);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(-10f); // this replaces setStartAtZero(true)
        rightAxis.setAxisMaximum(200f);

        Legend l = lineChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(1f);

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(15, false);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
    }

    public void buttonClickOnStop(View view) {
        List<String> menuList;
        audioRecordService.close();
        onStop();

        //update();
        dataItem = JSONHelper.importFromJSON(fileName);
        dataItem.setEndTime(getCurrentTime());
        JSONHelper.exportToJSON(fileName, dataItem);
        dataItem = null;

        if (!JSONHelper.fileExists("list.json")) {
            menuList = new ArrayList<>();
        } else {
            menuList = JSONHelper.importFromListJSON();
//            if (menuList == null) {
//                menuList = new ArrayList<>();
//            }
        }
        //Toast.makeText(this, fileName, Toast.LENGTH_SHORT).show();

        menuList.add(fileName);
        JSONHelper.exportToListJSON(menuList);

//        startActivity(new Intent(DrawActivity.this, MenuActivity.class));
//        finish();
    }

    private void update() {
        DataItem dataItem;
        if (soundModel.getSnoreCount() > snoreCount) {
            dataItem = JSONHelper.importFromJSON(fileName);
            dataItem.addSnore(getCurrentTime());
            snoreCount = soundModel.getSnoreCount();
            JSONHelper.exportToJSON(fileName, dataItem);
        }
        if (soundModel.getMovementCount() > movementCount) {
            dataItem = JSONHelper.importFromJSON(fileName);
            dataItem.addMovement(getCurrentTime());
            movementCount = soundModel.getMovementCount();
            JSONHelper.exportToJSON(fileName, dataItem);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(receiver, new IntentFilter(AudioRecordService.SERVICE_MESSAGE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(receiver);
    }

    /**
     * Start recording and get the datum
     */
    private void action() {
        audioRecordService.startActionFoo(this, "value1", "value2");
    }

    private void setLists() {
        lists.clear();
        for (int i = 1; i < 20; i++) {
            int value = ((int) (Math.random() * 100));
            lists.add(value);
        }
    }


    public Long getCurrentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        return Long.parseLong(simpleDateFormat.format(date));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.quit, menu);
        menu.add(1, Menu.FIRST, 1, "Menu");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.quit:
                super.finish();
                System.exit(0);
                return true;
            case 1:
                startActivity(new Intent(DrawActivity.this, MenuActivity.class));
                finish();
                return true;
            default:
                return false;
        }
    }
}
