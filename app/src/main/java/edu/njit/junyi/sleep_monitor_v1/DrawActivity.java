package edu.njit.junyi.sleep_monitor_v1;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import edu.njit.junyi.sleep_monitor_v1.Record.AudioRecorder;
import edu.njit.junyi.sleep_monitor_v1.Record.SoundCalculator;
import edu.njit.junyi.sleep_monitor_v1.Record.SoundModel;

/**
 * Created by junyi on 3/20/18.
 */

public class DrawActivity extends Activity {
    private static final String TAG1 = "DrawActivity1";
    private static final String TAG2 = "tag2";
    private static final String TAG3 = "tag3";
    private static final String TAG = "DrawActiviey";
    private boolean flag = true;

    private TextView rlh;
    private TextView rms;
    private TextView var;
    private SoundModel soundModel;
    private AudioRecorder audioRecorder;
    private Handler handlerRLH;
    private Handler handlerRMS;
    private Handler handlerVAR;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        rlh = (TextView) findViewById(R.id.rlh);
        rms = findViewById(R.id.rms);
        var = findViewById(R.id.var);
//        handlerRLH = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (flag) {
//                            rlh.setText("11111111");
//                            flag = false;
//                        } else {
//                            rlh.setText("22222222");
//                            flag = true;
//                        }
//                    }
//                });
////            if (soundModel.RLH.size() > 0) {
////                rlh.setText(Double.toString(soundModel.RLH.remove(0)));
////            }
//            }
//        };
        action();
        //showResult();
        //Log.d(TAG, "onCreate: succeed");
    }



    /**
     * Start recording and get the datum
     */
    private void action() {
        soundModel = new SoundModel();
        audioRecorder = new AudioRecorder(soundModel);
        audioRecorder.start();
    }

    /**
     * Show the values of ...
     */
    public void showResult() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                    Message msg = new Message();
                    handlerRLH.sendMessage(msg);
//                Message messageRLH = new Message(soundModel.RLH.remove(0));
//                if (soundModel.RLH.size() > 0) {
//                    handlerRLH.sendMessage(soundModel.RLH.remove(0));
//                }
                    rms.setText(Double.toString(soundModel.getLastRMS()));
                    var.setText(Double.toString(soundModel.getLastVAR()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}
