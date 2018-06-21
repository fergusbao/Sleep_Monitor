package edu.njit.junyi.sleep_monitor_v1.model;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by junyi on 3/20/18.
 */

public class SoundModel implements Serializable {
    private static final String TAG = "SoundModel";

    /**
     * RMS: The ratio of low frequency to high frequency
     * RLH: The root mean square - The average loudness of the frame
     * VAR: The volume variance of the frame
     */
    private List<Double> RLH;
    private List<Double> RMS;
    private List<Double> VAR;
    private List<Double> normalizedVAR;

    /**
     * Initialize two counters
     */
    private int snoreCount = 0;
    private int movementCount = 0;

    /**
     * Constructor
     * Initialize three array lists RMS, RLH, VAR
     */
    public SoundModel() {
        RLH = new ArrayList<>();
        RMS = new ArrayList<>();
        VAR = new ArrayList<>();
        normalizedVAR = new ArrayList<>();
    }

    public List<Double> getRLHList() {
        return RLH;
    }

    public List<Double> getRMSList() {
        return RMS;
    }

    public List<Double> getVARList() {
        return VAR;
    }

    public List<Double> getNormalizedVARList() {
        return normalizedVAR;
    }

    public double getRLH() {
        if (RLH.size() > 0) {
            return (double) Math.round(RLH.get(RLH.size()-1) * 100) / 100;
        }
        return 0f;
    }

    public double getRMS() {
        if (RMS.size() > 0) {
            return (double) Math.round(RMS.get(RMS.size()-1) * 100) / 100;
        }
        return 0f;
    }

    public double getVAR() {
        if (VAR.size() > 0) {
            return (double) Math.round(VAR.get(VAR.size()-1) * 100) / 100;
        }
        return 0f;
    }

    public double getNormalizedVAR() {
        if (normalizedVAR.size() > 0) {
            return (double) Math.round(normalizedVAR.remove(0) * 100) / 100;
        }
        return 0f;
    }

    public int getSnoreCount() {
        return snoreCount;
    }

    public int getMovementCount() {
        return movementCount;
    }

    /**
     * Add a data into the RLH list
     *
     * @param data
     */
    public void addRLH(Double data) {
        if (RLH.size() >= 100) {
            RLH.remove(0);
        }
        Log.i(TAG, "addRLH: " + data);
        RLH.add(data);

        if (normalizedVAR.size() >= 100) {
            normalizedVAR.remove(0);
        }
        normalizedVAR.add(calculateNormalizedVAR());
    }

    /**
     * Add a data into the RMS list
     *
     * @param data
     */
    public void addRMS(Double data) {
        if (RMS.size() >= 100) {
            RMS.remove(0);
        }
        Log.i(TAG, "addRMS: " + data);
        RMS.add(data);
    }

    /**
     * Add a data into the VAR list
     *
     * @param data
     */
    public void addVAR(Double data) {
        if (VAR.size() >= 100) {
            VAR.remove(0);
        }
        Log.i(TAG, "addVAR: " + data);
        VAR.add(data);
    }

    /**
     * Get the normalized RLH
     *
     * @return
     */
    public double calculateNormalizedRLH() {
        if (RLH.size() <= 1) {
            return 0d;
        }
        return (RLH.get(RLH.size() - 1) - mean(RLH)) / std(RLH);
    }

    /**
     * Get the normalized RMS
     *
     * @return
     */
    public double calculateNormalizedRMS() {
        if (RMS.size() <= 1) {
            return 0d;
        }
        return (RMS.get(RMS.size() - 1) - mean(RMS)) / std(RMS);
    }

    /**
     * Get the normalized VAR
     *
     * @return
     */
    public double calculateNormalizedVAR() {
        if (VAR.size() <= 1) {
            return 0d;
        }
        return (VAR.get(VAR.size() - 1) - mean(VAR)) / std(VAR);
    }

    /**
     * Get the last RLH data
     *
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
     *
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
     *
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
     *
     * @param list
     * @return
     */
    private double mean(List<Double> list) {
        double sum = 0;
        for (double num : list) {
            sum += num;
        }
        return sum / list.size();
    }

    /**
     * Get the standard deviation of the list
     *
     * @param list
     * @return
     */
    private double std(List<Double> list) {
        if (list.size() <= 1) {
            return 1d;
        }
        double mean = mean(list);
        double var = 0;
        for (double num : list) {
            var += Math.pow(num - mean, 2);
        }
        return Math.sqrt(var / list.size());
    }

    /**
     * This function detects which event occurred in the current frame
     */
    public void calculateFrame() {
//        if(calculateNormalizedVAR() > 1) { // Filter noise
//            if(calculateNormalizedRLH() > 1) {
//                snore++;
//            } else {
//                if(calculateNormalizedRMS() > 0.5) {
//                    movement++;
//                }
//            }
//        }
        if (getLastRLH() > 10d) {
            if (calculateNormalizedVAR() > 2d) {
                snoreCount++;
                Log.e("Event", "snore");
            }
        } else {
            if (getLastRMS() > 15d && calculateNormalizedVAR() > 0.5d
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
