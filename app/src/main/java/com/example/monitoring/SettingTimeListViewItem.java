package com.example.monitoring;

public class SettingTimeListViewItem {
    private String time;
    private boolean flag;
    public SettingTimeListActivity onCheckedListener;


    public SettingTimeListViewItem(){

    }
    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }
    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean getFlag() {
        return flag;
    }
}
