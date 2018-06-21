package edu.njit.junyi.sleep_monitor_v1.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import edu.njit.junyi.sleep_monitor_v1.R;

/**
 * Created by junyi on 3/2/18.
 */

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_MULTIPLE_REQUEST = 123;

    private boolean permissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setBackgroundDrawableResource(R.drawable.yellow);

        if (!permissionGranted) {
            checkPermissions();
        }
    }

    public void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this,
                            Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.RECORD_AUDIO},
                        PERMISSIONS_MULTIPLE_REQUEST);
            } else {
                // Permission has already been granted
                permissionGranted = true;
            }
        } else {
            // low versions do not need permission of users
            permissionGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST:
                if (grantResults.length > 0) {
                    boolean readExternalFile = grantResults[0]
                            == PackageManager.PERMISSION_GRANTED;
                    boolean writeExternalFile = grantResults[1]
                            == PackageManager.PERMISSION_GRANTED;
                    boolean audioRecord = grantResults[2]
                            == PackageManager.PERMISSION_GRANTED;

                    if (readExternalFile && writeExternalFile && audioRecord) {
                        permissionGranted = true;
                        Toast.makeText(this,
                                "Permissions of recording sound is granted!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this,
                                "Please Grant Permissions to record the sound!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    public void buttonClickOnEnter(View view) {
        if (!permissionGranted) {
            checkPermissions();
            return;
        }
        startActivity(new Intent(MainActivity.this, MenuActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.quit, menu);

        //menu.add(1, Menu.FIRST, 1, "Change Site ID");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.quit:
                super.finish();
                System.exit(0);
                return true;
            default:
                return false;
        }
    }
}
