package com.example.monitoring;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SettingTimeActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TimePicker startTime, endTime;
    Map<String, Object> setTime = new HashMap<>();
    private String TAG = "SettingTimeAcitivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_time);
        startTime = findViewById(R.id.timePickerStart);
        endTime = findViewById(R.id.timePickerEnd);
        startTime.setIs24HourView(true);
        endTime.setIs24HourView(true);


    }

    public void addTime(View view) {
        int sh,sm, eh,em;
        Calendar calStart = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        //String timetext;

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            calStart.set(Calendar.HOUR_OF_DAY,startTime.getHour());
            calStart.set(Calendar.MINUTE,startTime.getMinute());
            calEnd.set(Calendar.HOUR_OF_DAY,endTime.getHour());
            calEnd.set(Calendar.MINUTE,endTime.getMinute());
            //timetext = startTime.getHour()+":"+startTime.getMinute() + " ~ " +endTime.getHour()+":"+endTime.getMinute();

        }else{
            calStart.set(Calendar.HOUR_OF_DAY,startTime.getCurrentHour());
            calStart.set(Calendar.MINUTE,startTime.getCurrentMinute());
            calEnd.set(Calendar.HOUR_OF_DAY,endTime.getCurrentHour());
            calEnd.set(Calendar.MINUTE,endTime.getCurrentMinute());
            //timetext = calStart.get(Calendar.HOUR_OF_DAY)+":"+calStart.get(Calendar.MINUTE) + " ~ " +calEnd.get(Calendar.HOUR_OF_DAY)+":"+calEnd.get(Calendar.MINUTE);
        }

        String timetext = String.format("%02d", calStart.get(Calendar.HOUR_OF_DAY))+":"+String.format("%02d", calStart.get(Calendar.MINUTE)) + " ~ " +String.format("%02d", calEnd.get(Calendar.HOUR_OF_DAY))+":"+String.format("%02d", calEnd.get(Calendar.MINUTE));

        if(calStart.get(Calendar.HOUR_OF_DAY)>calEnd.get(Calendar.HOUR_OF_DAY))
            calEnd.add(Calendar.DATE,1);

//        calStart.add(Calendar.HOUR_OF_DAY,-9);
//        calEnd.add(Calendar.HOUR_OF_DAY,-9);//파이어스토어에서 우리가 확인할 때 시간 맞추기 위해



        setTime.put("timetext",timetext);
        setTime.put("start",calStart.getTime());
        setTime.put("end",calEnd.getTime());
        setTime.put("flag",true);
        setTime.put("now", new Timestamp(new Date()));

        db.collection("notificationTime")
                .add(setTime)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        Log.d(TAG, "DocumentSnapshot" + setTime);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

        Intent intent = new Intent(SettingTimeActivity.this,SettingTimeListActivity.class);
        startActivity(intent);
    }

}
