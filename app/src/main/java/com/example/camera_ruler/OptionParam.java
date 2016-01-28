package com.example.camera_ruler;

import java.io.Serializable;

public class OptionParam implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 3395663258368374909L;
    public int draw_alpha = (int) (255 * 0.5);
    public int lineWidth = 20;
    public int scaleWidth = 60;
    public int gapDist = 10;

    public boolean showShadow = true;
    public int shadowDeep = 2;
    public boolean showBaseline = true;
    public int gridLineCnt = 5;

    public boolean isShowShadow() {
        return showShadow;
    }

    public void setShowShadow(boolean showShadow) {
        this.showShadow = showShadow;
    }

    public boolean isShowBaseline() {
        return showBaseline;
    }

    public void setShowBaseline(boolean showBaseline) {
        this.showBaseline = showBaseline;
    }
}