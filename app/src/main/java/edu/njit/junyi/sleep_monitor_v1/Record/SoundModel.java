package edu.njit.junyi.sleep_monitor_v1.Record;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by junyi on 3/20/18.
 */

public class SoundModel {
    private static final String TAG1 = "tag1";
    private static final String TAG2 = "tag2";
    private static final String TAG3 = "tag3";

    /**
     * RMS: The ratio of low frequency to high frequency
     * RLH: The root mean square - The average loudness of the frame
     * VAR: The volume variance of the frame
     */
    public List<Double> RMS;
    public List<Double> RLH;
    public List<Double> VAR;

    private int snoreCount = 0;
    private int movementCount = 0;

    /**
     * Constructor
     */
    public SoundModel() {
        RMS = new ArrayList<>();
        RLH = new ArrayList<>();
        VAR = new ArrayList<>();
    }

    /**
     * Add a data into the RLH list
     * @param data
     */
    public void addRLH(Double data) {
        if (RLH.size() >= 100) {
            RLH.remove(0);
        }
        Log.i(TAG1, "addRLH: " + data);
        RLH.add(data);
    }

    /**
     * Add a data into the RMS list
     * @param data
     */
    public void addRMS(Double data) {
        if (RMS.size() >= 100) {
            RMS.remove(0);
        }
        Log.i(TAG2, "addRMS: " + data);
        RMS.add(data);
    }

    /**
     * Add a data into the VAR list
     * @param data
     */
    public void addVAR(Double data) {
        if (VAR.size() >= 100) {
            VAR.remove(0);
        }
        Log.i(TAG3, "addVAR: " + data);
        VAR.add(data);
    }

    /**
     * Get the normalized RLH
     * @return
     */
    public double getNormalizedRLH() {
        if (RLH.size() <= 1) {
            return 0d;
        }
        return (RLH.get(RLH.size() - 1) - mean(RLH)) / std(RLH);
    }

    /**
     * Get the normalized RMS
     * @return
     */
    public double getNormalizedRMS() {
        if (RMS.size() <= 1) {
            return 0d;
        }
        return (RMS.get(RMS.size() - 1) - mean(RMS)) / std(RMS);
    }

    /**
     * Get the normalized VAR
     * @return
     */
    public double getNormalizedVAR() {
        if (VAR.size() <= 1) {
            return 0d;
        }
        return (VAR.get(VAR.size() - 1) - mean(VAR)) / std(VAR);
    }

    /**
     * Get the last RLH data
     * @return
     */
    public double getLastRLH() {
        if (RLH.size() <= 1) {
            return 0d;
        }
        return RLH.get(RLH.size() - 1);
    }

    /**
     * Get the last RMS data
     * @return
     */
    public double getLastRMS() {
        if (RMS.size() <= 1) {
            return 0d;
        }
        return RMS.get(RMS.size() - 1);
    }

    /**
     * Get the last VAR data
     * @return
     */
    public double getLastVAR() {
        if (VAR.size() <= 1) {
            return 0d;
        }
        return VAR.get(VAR.size() - 1);
    }

    /**
     * Get the mean of the list
     * @param list
     * @return
     */
    private double mean(List<Double> list) {
        double sum = 0;
        for (double num: list) {
            sum += num;
        }
        return sum / list.size();
    }

    /**
     * Get the standard deviation of the list
     * @param list
     * @return
     */
    private double std(List<Double> list) {
        double mean = mean(list);
        double var = 0;
        for (double num: list) {
            var += Math.pow(num - mean, 2);
        }
        return Math.sqrt(var / list.size());
    }

    /**
     * This function detects which event occurred in the current frame
     */
    public void calculateFrame() {
//        if(getNormalizedVAR() > 1) { // Filter noise
//            if(getNormalizedRLH() > 1) {
//                snore++;
//            } else {
//                if(getNormalizedRMS() > 0.5) {
//                    movement++;
//                }
//            }
//        }
        if (getLastRLH() > 10d) {
            if (getNormalizedVAR() > 2d) {
                snoreCount++;
                Log.e("Event", "snore");
            }
        } else {
            if (getLastRMS() > 15d && getNormalizedVAR() > 0.5d
                    && Math.abs(getLastRLH()) > 1d) {
                movementCount++;
                Log.e("Event", "movement");
            }
        }
    }

    public int getEvent() {
    if (snoreCount > 5) {
            return 1;
        } else {
            if (movementCount > 1) {
                return 2;
            }
        }
        return 0;
    }

    public int getIntensity() {
        if (getEvent() == 1) {
        return snoreCount;
        } else if (getEvent() == 2) {
            return movementCount;
        }
        return 0;
    }

    public void resetEvents() {
        snoreCount = 0;
        movementCount = 0;
    }
}
