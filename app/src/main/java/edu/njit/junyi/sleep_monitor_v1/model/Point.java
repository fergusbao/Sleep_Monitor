package edu.njit.junyi.sleep_monitor_v1.model;

/**
 * Created by junyi on 4/17/18.
 */

public class Point {
    private float valueX;
    private float valueY;

    public Point(float valueX, float valueY) {
        this.valueX = valueX;
        this.valueY = valueY;
    }

    public float getValueX() {
        return valueX;
    }

    public void setValueX(float valueX) {
        this.valueX = valueX;
    }

    public float getValueY() {
        return valueY;
    }

    public void setValueY(float valueY) {
        this.valueY = valueY;
    }

    @Override
    public String toString() {
        return "Point{" +
                "valueX=" + valueX +
                ", valueY=" + valueY +
                '}';
    }
}
