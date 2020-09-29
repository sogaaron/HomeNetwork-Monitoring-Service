package com.example.monitoring;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SettingTimeListAdapter extends BaseAdapter {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Context context = null;
    LayoutInflater layoutInflater = null;
    private ArrayList<SettingTimeListViewItem> listViewItemList = null;
    private String TAG = "SettingTimeListAdapter";

    public SettingTimeListAdapter(ArrayList<SettingTimeListViewItem> _oData) {
        listViewItemList = _oData;
    }


    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Context context = parent.getContext();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
            SettingTimeListViewItem listViewItem = listViewItemList.get(position);
            //final Context context = parent.getContext();
            convertView = inflater.inflate(R.layout.listview_setting_time, parent, false);

            Switch sw = convertView.findViewById(R.id.switch1);
            //TextView timeTextView = convertView.findViewById(R.id.textView6);
            sw.setText(listViewItem.getTime());
            sw.setChecked(listViewItem.getFlag());
            sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String text=null;
                    Switch sw = buttonView.findViewById(R.id.switch1);
                    text = (String) sw.getText();

                    if(isChecked==true){
                        db.collection("notificationTime")
                                .whereEqualTo("timetext", text)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                db.collection("notificationTime").document(document.getId()).update("flag", true);
                                                Log.d(TAG, document.getId() + " => " + document.getData());
                                            }
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                    }
                                });
                    }else if(isChecked==false){
                        db.collection("notificationTime")
                                .whereEqualTo("timetext", text)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                db.collection("notificationTime").document(document.getId()).update("flag", false);
                                                Log.d(TAG, document.getId() + " => " + document.getData());
                                            }
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                    }
                                });
                    }
                }
            });
            //timeTextView.setText(listViewItem.getTime());

        }

        return convertView;
    }

/*
    public void addItem(String time, boolean flag) {
        SettingTimeListViewItem item= new SettingTimeListViewItem() ;

        item.setTime(time);
        item.setFlag(flag);
        Log.e(TAG,item.getTime() +"  "+ item.getFlag());
        listViewItemList.add(item);
    }

 */
}
