package com.example.monitoring;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class DeviceListAdapter extends BaseAdapter {

    LayoutInflater inflater = null;
    private ArrayList<DeviceListViewItem> m_oData = null;
    private int nListCnt = 0;

    public DeviceListAdapter(ArrayList<DeviceListViewItem> _oData){
        m_oData = _oData;
        nListCnt = m_oData.size();
    }

    @Override
    public int getCount() {
        Log.i("getCount",  String.valueOf(nListCnt));
        return nListCnt;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            final Context context = parent.getContext();
            if(inflater == null)
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_device_item, parent, false);
        }

        TextView oTextTitle = (TextView) convertView.findViewById(R.id.textView);
        Button oBtnU = (Button) convertView.findViewById(R.id.buttonUpdate);
        Button oBtnD = (Button) convertView.findViewById(R.id.buttonDelete);

        oBtnU.setTag("1");
        oBtnD.setTag("2");

        oTextTitle.setText(m_oData.get(position).nickname);
        oBtnU.setOnClickListener(m_oData.get(position).onClickListener);
        oBtnD.setOnClickListener(m_oData.get(position).onClickListener);

        convertView.setTag(""+position);
        return convertView;


    }
}
