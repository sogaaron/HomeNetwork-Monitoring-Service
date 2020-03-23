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
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            sh= startTime.getHour();
            sm = startTime.getMinute();
            eh= endTime.getHour();
            em = endTime.getMinute();
            Log.d(TAG, "time" + sh + ":" + sm + "    "+eh + ":" +em);
        }else{
            sh = startTime.getCurrentHour();
            sm = startTime.getCurrentMinute();
            eh= endTime.getCurrentHour();
            em = endTime.getCurrentMinute();
            Log.d(TAG, "time" + sh + ":" + sm + "    "+eh + ":" +em);

        }
        String timetext = sh+":"+sm + " ~ " +eh + ":"+em;
        setTime.put("timetext",timetext);
        setTime.put("start",new Timestamp(new Date(0,0,1, sh-9, sm,0)));
        setTime.put("end",new Timestamp(new Date(0,0,1, eh-9, em,0)));
        setTime.put("flag",false);


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
