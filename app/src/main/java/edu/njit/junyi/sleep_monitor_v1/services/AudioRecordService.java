package edu.njit.junyi.sleep_monitor_v1.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import edu.njit.junyi.sleep_monitor_v1.model.SoundCalculator;
import edu.njit.junyi.sleep_monitor_v1.model.SoundModel;
import edu.njit.junyi.sleep_monitor_v1.storage.DataItem;
import edu.njit.junyi.sleep_monitor_v1.storage.JSONHelper;

/**
 * Created by junyi on 3/22/18.
 */

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class AudioRecordService extends IntentService {
    private static final int SAMPLE_RATE_IN_HZ = 16000;
    private static final String TAG = "AudioRecorder";
    public static final String SERVICE_MESSAGE = "ServiceMessage";
    public static final String MESSAGE_KEY = "message";
    private static volatile boolean isRecording = true;

    private static AudioRecord recorder;
    private short[] buffer;
    private static int bufferSize;
    private SoundModel soundModel;
    private SoundCalculator soundCalculator;

    public AudioRecordService(SoundModel soundModel) {
        super("AudioRecordService");
        this.soundModel = soundModel;
        this.soundCalculator = new SoundCalculator(this.soundModel);
    }

    public AudioRecordService() {
        super("AudioRecordService");
        soundModel = new SoundModel();
        soundCalculator = new SoundCalculator(soundModel);
    }

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "edu.njit.junyi.sleep_monitor_v1.action.FOO";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "edu.njit.junyi.sleep_monitor_v1.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "edu.njit.junyi.sleep_monitor_v1.extra.PARAM2";

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, AudioRecordService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     * <p>
     * Start recording the sound and pass the datum into sound calculator
     */
    private void handleActionFoo(String param1, String param2) {
        // Standard priority of the most important audio threads.
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        initRecorder();
        recorder.startRecording();

        while (isRecording) {
            int readSize = recorder.read(buffer, 0, buffer.length);
            process(buffer);
            sendMessage(soundModel);
        }
        recorder.stop();
        recorder.release();
    }

    /**
     * Initialize the AudioRecord object
     */
    public void initRecorder() {
        Log.i(TAG, "initRecorder: " + bufferSize);
        if (bufferSize == 0 || recorder == null ||
                recorder.getState() != AudioRecord.STATE_INITIALIZED) {
            bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            Log.i(TAG, "bufferSize: " + bufferSize);
            buffer = new short[bufferSize];

            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE_IN_HZ,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize);

            Log.i(TAG, "initRecorder: " + bufferSize);
        }
    }

    /**
     * @param buffer
     */
    private void process(short[] buffer) {
        soundCalculator.update(buffer);
    }

    /**
     * Stop the service thread
     */
    public void start() {
        isRecording = true;
    }

    /**
     * Stop the service thread
     */
    public void close() {
        isRecording = false;
    }

    /**
     * Broadcast the data
     * @param message SoundModel object
     */
    public void sendMessage(SoundModel message) {
        Intent intent = new Intent(SERVICE_MESSAGE);
        intent.putExtra(MESSAGE_KEY, message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
