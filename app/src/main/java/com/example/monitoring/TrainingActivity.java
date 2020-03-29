package com.example.monitoring;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.BLUE;
import static android.graphics.Color.RED;


public class TrainingActivity extends AppCompatActivity {
    TextView textView;
    final Handler handler = new Handler();
    int i=0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String TAG = "TrainingActivity";
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    ArrayList<Calendar> onTime = new ArrayList<>();
    ArrayList<Calendar> offTime = new ArrayList<>();
    ArrayList<Integer> onTraffic = new ArrayList<>();
    ArrayList<Integer> offTraffic = new ArrayList<>();
    int onSum, offSum = 0;
    double event = 0;



    //Calendar[] onTime = new Calendar[5];
    //Calendar[] offTime = new Calendar[4];







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        textView = findViewById(R.id.textViewTraining);

        Intent intent = getIntent();

        final String mac = intent.getExtras().getString("mac");

        new Handler().postDelayed(new Runnable() {

            @Override

            public void run() {
                if (i!=3) {
                    textView.setText("기기를 작동하십시오");
                    textView.setTextColor(RED);
                    //Log.d(TAG, "기기작동"+ i);


                    Calendar now = Calendar.getInstance();
                    now.add(Calendar.HOUR_OF_DAY,9);
                    onTime.add(now);
                    //Log.d(TAG,"now" + now);

                    new Handler().postDelayed(new Runnable() {

                        @Override

                        public void run() {
                            i++;

                            if (i==3) {
                                textView.setText("잠시만 기다려주세요.");
                                textView.setTextColor(BLUE);
                      //          Log.d(TAG, "측정 끝"+ i);
                                check(mac);

                            } else{
                                textView.setText("기기를 가만히 두십시오");
                                textView.setTextColor(BLACK);

                        //        Log.d(TAG, "기기멈춤"+ i);
                            }

                            Calendar now = Calendar.getInstance();
                            now.add(Calendar.HOUR_OF_DAY,9);
                            offTime.add(now);
                     //       Log.d(TAG,"now" + now);

/*
                            int i = 0;

                            for(Calendar value : onTime){
                                Log.d(TAG,  "ontime : " +i+". " + value.getTime());
                                i++;
                            }
                            int j = 0;
                            for(Calendar value : offTime){
                                Log.d(TAG,  "offtime : "+ j+". " + value.getTime());
                                j++;
                            }
*/

                        }
                    }, 8000); // ms 단위라서 1000이 1초입니다.
                    handler.postDelayed(this,18000);
                }
            }
        }, 3000); // ms 단위라서 1000이 1초입니다.








    }

    private void check(final String mac) {
Log.d(TAG, "MAC : "+mac);

        db.collection("test")
        //        .whereEqualTo("nickname", selectedNickname)
                .orderBy("time", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                int traffic = document.getLong("gabriel").intValue();
                                String time = document.getString("time");

                                Calendar cal = Calendar.getInstance();
                                try {
                                    Date date = dateFormat.parse(time);
                                    cal.setTime(date);
//                                    Log.d(TAG, "string to cal" + cal.getTime());

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                Log.d(TAG, "0. " + onTime.get(0).getTime() + "  ~ "+ cal.getTime() + "  ~ " + offTime.get(0).getTime());
                                Log.d(TAG, String.valueOf(cal.after(onTime.get(0)) && cal.before(offTime.get(0))));

                                Log.d(TAG, "1. " + onTime.get(1).getTime() + "  ~ "+ cal.getTime() + "  ~ " + offTime.get(1).getTime());
                                Log.d(TAG, String.valueOf(cal.after(onTime.get(0)) && cal.before(offTime.get(0))));

                                Log.d(TAG, "2. " + onTime.get(2).getTime() + "  ~ "+ cal.getTime() + "  ~ " +offTime.get(2).getTime());
                                Log.d(TAG, String.valueOf(cal.after(onTime.get(0)) && cal.before(offTime.get(0))));



                                if((cal.after(onTime.get(0)) && cal.before(offTime.get(0)))|| (cal.after(onTime.get(1)) && cal.before(offTime.get(1))) || (cal.after(onTime.get(2)) && cal.before(offTime.get(2)))){
                                    //작동할 때의 traffic
                                    onTraffic.add(traffic);
                                }else offTraffic.add(traffic);
                                //Log.d(TAG,"traffic DATE" +time + "       " + date);

                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }

                        int i = 0;

                        for(Integer value : onTraffic){
                            Log.d(TAG,  "ontime : " +i+". " + value.intValue());
                            onSum += value.intValue();
                            i++;
                        }
                        int j = 0;
                        for(Integer value : offTraffic){
                            Log.d(TAG,  "offtime : "+ j+". " + value.intValue());
                            offSum += value.intValue();
                            j++;
                        }

                        double onAverage = onSum / onTraffic.size();
                        double offAverage = offSum / offTraffic.size();

                        event = (onAverage + offAverage) / 2;
                        Log.d(TAG,"event : " + event);
                        db.collection("gabriel")
                                .whereEqualTo("mac", mac)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                db.collection("gabriel").document(document.getId()).update("normal", event);
                                                Log.d(TAG, document.getId() + " => " + document.getData());
                                            }

                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                        AlertDialog.Builder editAD = new AlertDialog.Builder(TrainingActivity.this);
                                        editAD.setTitle("")
                                                .setMessage("이벤트 등록이 완료되었습니다.\n" + mac + " event : " + event)
                                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent(TrainingActivity.this,MenuActivity.class);
                                                        startActivity(intent);
                                                    }
                                                });
                                        AlertDialog alert = editAD.create();
                                        alert.show();
                                    }
                                });

                    }
                });


    }


}

/*

    long deleyTime = 5000;
    long lastClickedTime = 0;

    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    timeCheck(timestamp.getTime());


    public void timeCheck(long nowTime)
    {
        int i=0;

        while (i==3) {
            if (lastClickedTime + deleyTime < nowTime)

                if (lastClickedTime == 0) { //처음클릭
                    lastClickedTime = nowTime;
                    //textView.setText("기기를 작동해주십시오.");
                    //A(); // 요기서 A실행
                } else {
                    if (lastClickedTime + deleyTime < nowTime) { // 5초 검증
                        Toast.makeText(this, "아직 5초 안지남", Toast.LENGTH_LONG).show();
                    } else {
                        lastClickedTime = nowTime;
                        A(); // 요기서 A실행
                    }
                }
        }}

 */


/*
String now = dateFormat.format(System.currentTimeMillis());
                            Log.d(TAG,"now" + now);

                            Calendar cal = Calendar.getInstance();
                            try {
                                Date date = dateFormat.parse(now);
                                cal.setTime(date);
                                offTime.add(cal);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

 */