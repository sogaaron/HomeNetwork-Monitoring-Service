package com.example.monitoring;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class SettingTimeListActivity extends AppCompatActivity{
    //ArrayList<String> arrayList;
    SettingTimeListAdapter adapter = new SettingTimeListAdapter();
    SettingTimeListAdapter2 adapter2 = new SettingTimeListAdapter2();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String TAG = "SettingTimeListActivity";
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_time_list);

        //arrayList = new ArrayList<>();

        db.collection("notificationTime")//.orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                /*

                                Timestamp timestampStart = document.getTimestamp("start");
                                Timestamp timestampEnd = document.getTimestamp("end");

                                 */
                                boolean flag = document.getBoolean("flag");
                                String time = document.getString("timetext");

                                //시간변환
                                /*
                                Date date1 = timestampStart.toDate();
                                Date date2 = timestampEnd.toDate();
                                Calendar cal1 = Calendar.getInstance();

                                Calendar cal2 = Calendar.getInstance();
                                cal1.setTime(date1);
                                cal2.setTime(date2);
                                cal1.add(Calendar.HOUR_OF_DAY,9);
                                cal2.add(Calendar.HOUR_OF_DAY,9);

                                String start = dateFormat.format(cal1.getTime());
                                String end = dateFormat.format(cal2.getTime());
                                Log.d("SettingTimeAcitivity", "show" + start + "  " + end);

                                 */
                                adapter.addItem(time,flag);
                                adapter2.addItem(time,flag);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        ListView listview;
                        listview = findViewById(R.id.listviewTime);
                        listview.setAdapter(adapter);

                    }
                });

    }

    public void addMode(View view) {
        Intent intent = new Intent(SettingTimeListActivity.this,SettingTimeActivity.class);
        startActivity(intent);
    }

    public void deleteMode(View view) {
        ListView listview;
        listview = findViewById(R.id.listviewTime);
        listview.setAdapter(adapter2);
    }
}
