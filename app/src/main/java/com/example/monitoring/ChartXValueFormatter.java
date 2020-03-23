package com.example.monitoring;

import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.List;

public class ChartXValueFormatter implements IAxisValueFormatter {

    private List<String> arr;

    ChartXValueFormatter(List<String> list){
        this.arr = list;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        String tmp = null;
        try {
            tmp = arr.get((int)value);
        }catch (IndexOutOfBoundsException e){
            if(value != 400 && value != 600)
                e.getMessage();
            else
                Log.e("aaron","intended error");
        }

        return tmp;
    }


}
