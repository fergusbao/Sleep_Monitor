package edu.njit.junyi.sleep_monitor_v1.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import edu.njit.junyi.sleep_monitor_v1.R;
import edu.njit.junyi.sleep_monitor_v1.storage.JSONHelper;

/**
 * Created by junyi on 3/19/18.
 */

public class MenuActivity extends AppCompatActivity {

    private List<String> menuList;
    private HashMap<String, String> fileList;
    private final static String FILE_NAME = "list.json";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        menuList = new ArrayList<>();
        fileList = new HashMap<>();

        List<String> tempList;
        if (JSONHelper.fileExists(FILE_NAME)) {

            tempList = JSONHelper.importFromListJSON();
            for (String time : tempList) {
                Long temp = Long.parseLong(time.replace(".json", ""));
                String second = Long.toString(temp % 100);
                if (second.length() < 2) {
                    second = "0" + second;
                }
                temp = temp / 100;
                String minute = Long.toString(temp % 100);
                if (minute.length() < 2) {
                    minute = "0" + minute;
                }
                temp = temp / 100;
                String hour = Long.toString(temp % 100);
                if (hour.length() < 2) {
                    hour = "0" + hour;
                }
                temp = temp / 100;
                String day = Long.toString(temp % 100);
                if (day.length() < 2) {
                    day = "0" + day;
                }
                temp = temp / 100;
                String month = Long.toString(temp % 100);
                if (month.length() < 2) {
                    month = "0" + month;
                }
                String year = Long.toString(temp / 100);
                String title = "Date: " + month + "/" + day + "/" + year
                        + "  Sleep at: " + hour + ":" + minute + ":" + second;
//                Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
                menuList.add(0, title);
                fileList.put(title, time);
            }
        }

//        Collections.sort(menuList);
//        String[] from = new String[menuList.size()];
//        for (int i = 0; i < menuList.size(); i++) {
//            from[i] = menuList.get(i);
//        }

        // Each row in the list stores date and image
        List<TreeMap<String, String>> aList = new ArrayList<>();

        for (int i = 0; i < menuList.size(); i++) {
            TreeMap<String, String> hm = new TreeMap<>();
            hm.put("time", menuList.get(i));
            hm.put("image", Integer.toString(R.drawable.ic_file_document));
            aList.add(hm);
        }

        // Keys used in Hashmap
        String[] from = {"time", "image"};

        // Ids of views in list_item layout
        int[] to = {R.id.tvNote, R.id.imageDocIcon};

        // Instantiating an adapter to store each items
        // R.layout.list_item layout defines the layout of each item
        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), aList, R.layout.list_item, from, to);

//        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                this, R.layout.array_adapter, menuList);

        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);

        // Item Click Listener for the list view
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View container, int position, long id) {
                // Getting the Container Layout of the ListView
                LinearLayout linearLayoutParent = (LinearLayout) container;

//                // Getting the inner Linear Layout
//                LinearLayout linearLayoutChild = (LinearLayout) linearLayoutParent.getChildAt(0);

//                // Getting the Country TextView
//                TextView tvCountry = (TextView) linearLayoutChild.getChildAt(0);
                TextView titleName = (TextView) linearLayoutParent.getChildAt(0);

//                Toast.makeText(getBaseContext(), titleName.getText().toString(), Toast.LENGTH_SHORT).show();
                goToDetail(titleName.getText().toString());
            }
        };

        // Setting the item click listener for the listview
        listView.setOnItemClickListener(itemClickListener);


    }

    // Pass the file name to the DetailActivity
    public void goToDetail(String filename) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("nv_name", fileList.get(filename));
        Bundle bundle = new Bundle();
        bundle.putString("nan_name", "test");
        intent.putExtra("bundle", bundle);
        startActivity(intent);
        finish();
    }

    public void buttonClickOnStart(View view) {
        startActivity(new Intent(MenuActivity.this, DrawActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.quit, menu);
        menu.add(1, Menu.FIRST, 1, "Home");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.quit:
                finish();
                System.exit(0);
                return true;
            case 1:
                startActivity(new Intent(MenuActivity.this, MainActivity.class));
                finish();
            default:
                return false;
        }
    }
}
