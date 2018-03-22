package edu.njit.junyi.sleep_monitor_v1.Record;

/**
 * Created by junyi on 3/20/18.
 */

public class SoundCalculator {
    private SoundModel soundModel;
    private float[] lowFreq;
    private float[] highFreq;
    private final static float a = 0.25f;

    /**
     * Constructor
     */
    public SoundCalculator(SoundModel soundModel) {
        this.soundModel = soundModel;
    }

    /**
     * Pass the calculated RLH, RMS, VAR datum into their arraylist,
     * Then detect the events(snore or movement)
     * @param buffer
     */
    public void update(short[] buffer) {
        lowFreq = new float[buffer.length];
        highFreq = new float[buffer.length];
        soundModel.addRLH(calculateRLH(buffer));
        soundModel.addRMS(calculateRMS(buffer));
        soundModel.addVAR(calculateVAR(buffer));

        soundModel.calculateFrame();
    }

    /**
     * Get the RLH data of one frame
     * @param buffer
     * @return
     */
    private double calculateRLH(short[] buffer) {
        double highRreqRMS = calculateHighFreqRMS(buffer);
        double lowRreqRMS = calculateLowFreqRMS(buffer);
        if (highRreqRMS == 0 || lowRreqRMS == 0) {
            return 0;
        } else {
            return lowRreqRMS / highRreqRMS;
        }
    }

    /**
     * Get the RMS data of one frame
     * @param buffer
     * @return
     */
    private double calculateRMS(short[] buffer) {
        long sum = 0;
        for (short num : buffer) {
            sum += Math.pow(num, 2);
        }
        return Math.sqrt(sum / buffer.length);
    }

    /**
     * Get the RMS data of one frame
     * @param buffer
     * @return
     */
    private double calculateRMS(float[] buffer) {
        long sum = 0;
        for (float num : buffer) {
            sum += Math.pow(num, 2);
        }
        return Math.sqrt(sum / buffer.length);
    }

    /**
     * Transform the original buffer datum into the low frequency RMS datum
     * @param buffer
     * @return
     */
    private double calculateLowFreqRMS(short[] buffer) {
        lowFreq[0] = 0;
        for (int i = 1; i < buffer.length; i++) {
            lowFreq[i] = lowFreq[i - 1] + a * (buffer[i] - lowFreq[i - 1]);
        }
        return calculateRMS(lowFreq);
    }

    /**
     * Transform the original buffer datum into the high frequency RMS datum
     * @param buffer
     * @return
     */
    private double calculateHighFreqRMS(short[] buffer) {
        highFreq[0] = 0;
        for (int i = 1; i < buffer.length; i++) {
            highFreq[i] = a * (highFreq[i - 1] + buffer[i] - buffer[i - 1]);
        }
        return calculateRMS(highFreq);
    }

    /**
     * Get the VAR data of one frame
     * @param buffer
     * @return
     */
    private double calculateVAR(short[] buffer) {
        double mean = calculateMean(buffer);
        double var = 0;
        for (short num : buffer) {
            var += Math.pow(num - mean, 2);
        }
        return var / buffer.length;
    }

    /**
     * Get the mean of one frame
     * @param buffer
     * @return
     */
    private double calculateMean(short[] buffer) {
        double mean = 0;
        for (short num : buffer) {
            mean += num;
        }
        return mean / buffer.length;
    }
}
