package edu.njit.junyi.sleep_monitor_v1.storage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by junyi on 4/3/18.
 */

public class DataItem {
    private Long startTime;
    private List<Long> snore;
    private List<Long> movement;
    private Long endTime;


    public DataItem(Long startTime) {
        this.startTime = startTime;
        this.snore = new ArrayList<>();
        this.movement = new ArrayList<>();
        this.endTime = 0l;
    }


    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public List<Long> getSnore() {
        return snore;
    }

    public void setSnore(List<Long> snore) {
        this.snore = snore;
    }

    public List<Long> getMovement() {
        return movement;
    }

    public void setMovement(List<Long> movement) {
        this.movement = movement;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public void addSnore(Long time) {
        snore.add(time);
    }

    public void addMovement(Long time) {
        movement.add(time);
    }
}
