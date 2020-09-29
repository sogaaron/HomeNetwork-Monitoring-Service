package com.example.monitoring;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LogActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    LogListAdapter adapter = new LogListAdapter();
    private String TAG = "LogActivity";
    SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    String date4 = null;

    private Spinner spinnerLog;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        getDeviceList();
    }

    private void getDeviceList() {
        arrayList = new ArrayList<>();
        arrayList.add("all");
        db.collection("gabriel")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String nickname = document.getString("nickname");

                                arrayList.add(nickname);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        spinnerLog = (Spinner)findViewById(R.id.spinnerLog);
                        arrayAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,arrayList);
                        spinnerLog.setAdapter(arrayAdapter);
                        spinnerLog.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String selectedNickname = (String) arrayList.get(position);
                                if(selectedNickname.equals("all")){
                                    allLogs();
                                }else{
                                    selectedLogs(selectedNickname);
                                }
                                Toast.makeText(getApplicationContext(),arrayList.get(position)+"가 선택되었습니다.",
                                        Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                });



    }


    private void list() {
        ListView listview;
        listview = findViewById(R.id.listviewLog);
        listview.setAdapter(adapter);
    }

    private void allLogs() {
        adapter.deleteItem();
        db.collection("log").orderBy("time",Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                String text = document.getString("text");
                                Timestamp timestamp = document.getTimestamp("time");

                                //시간변환
                                Date date1 = timestamp.toDate();

                                Calendar cal = Calendar.getInstance();
                                cal.setTime(date1);
                                cal.add(Calendar.HOUR,9);

                                String date2 = dateFormat1.format(cal.getTime());
                                String date3 = dateFormat2.format(cal.getTime());

                                if(date2.equals(date4)){
                                    adapter.addItem(text,date3);
                                }else {
                                  adapter.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.update), date2);
                                    //adapter.addItem(text,test);
                                    adapter.addItem(text,date3);
                                   date4 = date2;
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        list();


                    }
                });
    }


    private void selectedLogs(final String selectedNickname) {
        adapter.deleteItem();
        date4 = null;
        db.collection("log")
                .whereEqualTo("nickname", selectedNickname)
                .orderBy("time",Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String text = document.getString("text");
                                Timestamp timestamp = document.getTimestamp("time");

                                //시간변환
                                Date date1 = timestamp.toDate();
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(date1);
                                cal.add(Calendar.HOUR,9);

                                String date2 = dateFormat1.format(cal.getTime());
                                String date3 = dateFormat2.format(cal.getTime());

                                if(date2.equals(date4)){
                                    adapter.addItem(text,date3);
                                }else {
                                    adapter.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.update), date2);
                                    adapter.addItem(text,date3);
                                    date4 = date2;
                                }
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        list();

                    }
                });

    }


}
