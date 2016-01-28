package com.example.camera_ruler;

import java.io.InputStreamReader;
import android.R.string;
import android.graphics.Point;

class SideInfo {
    static public final int LEFT_SIDE = 1;
    static public final int RIGHT_SIDE = -1;
    static public final int ALL = 0;

    SideInfo(int pos) {
        sidePos = pos;

    }

    private Point p0 = new Point(); // 0.0m
    private Point p1 = new Point(); // 1.5m
    private double distA = 0.4;
    private double distB = 0.7;
    private double distMin = 0.15;
    private int minLength = 150;
    private int sidePos = LEFT_SIDE; //left: 1, right: -1

    // auto generate
    private Point p05 = new Point();
    private Point p15 = new Point();

    public Point getP0() {
        return p0;
    }

    public void setP0(Point p0) {
        this.p0.x = p0.x;
        this.p0.y = p0.y;

    }

    public Point getP1() {
        return p1;
    }

    public void setP1(Point p1) {
        this.p1.x = p1.x;
        this.p1.y = p1.y;

    }

    public double getDistA() {
        return distA;
    }

    public void setDistA(double distA) {
        this.distA = distA;
    }

    public double getDistB() {
        return distB;
    }

    public void setDistB(double distB) {
        this.distB = distB;
    }

    public Point getP05() {
        return p05;
    }

    public void setP05(Point p05) {
        this.p05.x = p05.x;
        this.p05.y = p05.y;
    }

    public Point getP15() {
        return p15;
    }

    public void setP15(Point p15) {
        this.p15.x = p15.x;
        this.p15.y = p15.y;
    }

    public int getSidePos() {
        return sidePos;
    }

    public void setSidePos(int sidePos) {
        this.sidePos = sidePos;
    }


    public String toString() {
        return String.format("%d,%d,%d,%d,%.2f,%.2f", p0.x, p0.y, p1.x, p1.y, distA, distB);

    }

    public boolean fromString(String str) {
        String strs[] = str.split("\\,");
        if (strs.length != 6) return false;
        p0.x = Integer.parseInt(strs[0]);
        p0.y = Integer.parseInt(strs[1]);
        p1.x = Integer.parseInt(strs[2]);
        p1.y = Integer.parseInt(strs[3]);
        distA = Double.parseDouble(strs[4]);
        distB = Double.parseDouble(strs[5]);
        return true;
    }

    public void CalcP0(Point p) {
        if (p.y < p1.y + minLength)
            p.y = p1.y + minLength;

        setP0(p);

    }

    public void CalcP05(Point p) {

        distA = (double) (p.y - p0.y) / (p1.y - p0.y);
        if (distA > distB - distMin)
            distA = distB - distMin;
        if (distA < distMin) distA = distMin;
    }

    public void CalcP15(Point p) {
        distB = (double) (p.y - p0.y) / (p1.y - p0.y);
        if (distB < distA + distMin)
            distB = distA + distMin;
        if (distB > 1 - distMin) distB = 1 - distMin;
    }

    public void CalcP1(Point p) {
        if (p.y > p0.y - minLength)
            p.y = p0.y - minLength;
        setP1(p);

    }
}