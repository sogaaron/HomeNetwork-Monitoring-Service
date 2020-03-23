package com.example.monitoring;

import android.graphics.drawable.Drawable;

public class LogListViewItem {
    private int type;

    private Drawable iconDrawable;
    private String dateStr;

    private String contentStr;
    private String timeStr;

    public void setType(int type) {
        this.type = type;
    }

    public void setIcon(Drawable iconDrawable) {
        this.iconDrawable = iconDrawable;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public void setContentStr(String contentStr) {
        this.contentStr = contentStr;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }

    public int getType() {
        return type;
    }

    public Drawable getIcon() {
        return iconDrawable;
    }

    public String getDateStr() {
        return dateStr;
    }

    public String getContentStr() {
        return contentStr;
    }

    public String getTimeStr() {
        return timeStr;
    }


}
