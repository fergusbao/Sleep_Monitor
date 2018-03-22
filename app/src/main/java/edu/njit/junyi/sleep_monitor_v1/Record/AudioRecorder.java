package edu.njit.junyi.sleep_monitor_v1.Record;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * Created by junyi on 3/20/18.
 */

public class AudioRecorder extends Thread {
    private static final int SAMPLE_RATE_IN_HZ = 16000;
    private static final String TAG = "AudioRecord";
    private boolean isRecording = true;

    private static AudioRecord recorder;
    private short[] buffer;
    private static int bufferSize;
    private SoundModel soundModel;
    private SoundCalculator soundCalculator;

    public AudioRecorder(SoundModel soundModel) {
        this.soundModel = soundModel;
        this.soundCalculator = new SoundCalculator(soundModel);
    }

    @Override
    public void run() {
        capture();
    }

    /**
     * Start recording the sound and pass the datum into sound calculator
     */
    private void capture() {
        // Standard priority of the most important audio threads.
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        initRecorder();
        recorder.startRecording();

        while (isRecording) {
            int readSize = recorder.read(buffer, 0, buffer.length);
            process(buffer);
        }
        recorder.stop();
        recorder.release();
    }

    /**
     * Initialize the AudioRecord object
     */
    public void initRecorder() {
        Log.i(TAG, "initRecorder: " + bufferSize);
        if (bufferSize == 0 || recorder == null || recorder.getState() != AudioRecord.STATE_INITIALIZED) {
            bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);

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
     *
     * @param buffer
     */
    private void process(short[] buffer) {
        soundCalculator.update(buffer);
    }

    /**
     * s
     */
    public void close() {
        isRecording = false;
    }

}
