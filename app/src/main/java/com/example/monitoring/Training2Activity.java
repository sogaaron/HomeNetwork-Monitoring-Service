package com.example.monitoring;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.graphics.Color.LTGRAY;
import static android.graphics.Color.RED;


public class Training2Activity extends AppCompatActivity {
    TextView textView;
    Button button;

    final Handler handler = new Handler();
    int i=0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String TAG = "TrainingActivity";
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    ArrayList<Calendar> onTime = new ArrayList<>();
    ArrayList<Calendar> offTime = new ArrayList<>();
    ArrayList<Integer> onTraffic = new ArrayList<>();
    ArrayList<Integer> offTraffic = new ArrayList<>();
    ArrayList<Integer> allTraffic = new ArrayList<>();

    ArrayList<Integer> onCommand = new ArrayList<>();
    ArrayList<Integer> offCommand = new ArrayList<>();
    ArrayList<Integer> allCommand = new ArrayList<>();

    int onSumTraffic, offSumTraffic = 0;
    int eventTraffic = 0;

    int onSumCommand, offSumCommand = 0;
    int eventCommand = 0;

    int finalEvent = 0;

    int times = 0;
    String mac;
    int type = 0;





    //Calendar[] onTime = new Calendar[5];
    //Calendar[] offTime = new Calendar[4];







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training2);
        textView = findViewById(R.id.textViewTraining);
        button = findViewById(R.id.buttonT);

        Intent intent = getIntent();

        mac = intent.getExtras().getString("mac");




//        check(mac);
    }

    private void check(final String mac) {
        Log.d(TAG, "MAC : "+mac);

        db.collection("traffics")
                //        .whereEqualTo("nickname", selectedNickname)
                .orderBy("time", Query.Direction.DESCENDING)
                .limit(6)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try {
                                    List ls = (List)document.getData().get(mac);
                                    int traffic = Integer.parseInt(ls.get(0).toString());
                                    int command = Integer.parseInt(ls.get(1).toString());
                                    String time = document.getString("time");

                                    Calendar cal = Calendar.getInstance();
                                    try {
                                        Date date = dateFormat.parse(time);
                                        cal.setTime(date);
//                                    Log.d(TAG, "string to cal" + cal.getTime());

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    allTraffic.add(traffic);
                                    allCommand.add(command);
                                    //time
                                    Log.d(TAG, "0. " + onTime.get(0).getTime() + "  ~ "+ cal.getTime() + "  ~ " + offTime.get(0).getTime());
                                    Log.d(TAG, String.valueOf(cal.after(onTime.get(0)) && cal.before(offTime.get(0))));

                                    Log.d(TAG, "1. " + onTime.get(1).getTime() + "  ~ "+ cal.getTime() + "  ~ " + offTime.get(1).getTime());
                                    Log.d(TAG, String.valueOf(cal.after(onTime.get(1)) && cal.before(offTime.get(1))));

                                    Log.d(TAG, "2. " + onTime.get(2).getTime() + "  ~ "+ cal.getTime() + "  ~ " +offTime.get(2).getTime());
                                    Log.d(TAG, String.valueOf(cal.after(onTime.get(2)) && cal.before(offTime.get(2))));

                                    if((cal.after(onTime.get(0)) && cal.before(offTime.get(0)))|| (cal.after(onTime.get(1)) && cal.before(offTime.get(1))) || (cal.after(onTime.get(2)) && cal.before(offTime.get(2)))){
                                        //작동할 때의 traffic
                                        onTraffic.add(traffic);
                                        onCommand.add(command);
                                        Log.d(TAG,"onTraffic" + onTraffic);
                                    }else {
                                        offTraffic.add(traffic);
                                        offCommand.add(command);
                                        Log.d(TAG,"offTraffic" + offTraffic);

                                    }
                                    //Log.d(TAG,"traffic DATE" +time + "       " + date);

                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    //time
                                }catch (NullPointerException e){
                                    Log.e("err",e.getMessage());
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }

                        Collections.sort(onTraffic);
                        Collections.sort(offTraffic);
                        Collections.reverse(offTraffic);

                        Collections.sort(onCommand);
                        Collections.sort(offCommand);
                        Collections.reverse(onCommand);

                        onTraffic.remove(0);
                        offTraffic.remove(0);

                        onCommand.remove(0);
                        offCommand.remove(0);

                        int i = 0;
                        for(Integer value : onTraffic){
                            Log.d(TAG,  "ontimeTraffic : " +i+". " + value.intValue());
                            onSumTraffic += value.intValue();
                            i++;
                        }
                        int j = 0;
                        for(Integer value : offTraffic){
                            Log.d(TAG,  "offtimeTraffic : "+ j+". " + value.intValue());
                            offSumTraffic += value.intValue();
                            j++;
                        }
                        int m = 0;
                        for(Integer value : onCommand){
                            Log.d(TAG,  "ontimeCommand : " +m+". " + value.intValue());
                            onSumCommand += value.intValue();
                            m++;
                        }
                        int n = 0;
                        for(Integer value : offCommand){
                            Log.d(TAG,  "offtimeCommand : "+ n+". " + value.intValue());
                            offSumCommand += value.intValue();
                            n++;
                        }


/*                        // all traffic
                        Collections.sort(allTraffic);
                        Collections.reverse(allTraffic);

                        int max = 0;
                        int maxIndex=0;

                        for (int t = 0; t<allTraffic.size()-1; t++){
                            int d = allTraffic.get(t) - allTraffic.get(t+1);
                            if (d>max){
                                max = d;
                                maxIndex = t;
                            }
                        }
                        event = (double)max/3 + allTraffic.get(maxIndex+1);
                        Log.d(TAG,"max " +  allTraffic.get(maxIndex) + "  d "+ max + "  event  " + event);
                        // all traffic
*/

                        //average
                        double onAverageTraffic = onSumTraffic / onTraffic.size();
                        double offAverageTraffic = offSumTraffic / offTraffic.size();

                        double onAverageCommand = onSumCommand / onCommand.size();
                        double offAverageCommand = offSumTraffic / offTraffic.size();

                        eventTraffic = (int) ((onAverageTraffic + offAverageTraffic) / 2);
                        eventCommand = (int) ((onAverageCommand + offAverageCommand) / 2);

                        Log.d(TAG,"eventTraffic : " + eventTraffic + "  eventCommand" + eventCommand);
                        //average

                        if (eventTraffic-eventCommand>1000){
                            type=0;
                            finalEvent = eventTraffic;
                        }else{
                            type=1;
                            finalEvent = eventCommand;
                        }

                        db.collection("gabriel")
                                .whereEqualTo("mac", mac)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                db.collection("gabriel").document(document.getId()).update("normal", finalEvent);
                                                db.collection("gabriel").document(document.getId()).update("type", type);
                                                Log.d(TAG, document.getId() + " => " + document.getData());
                                            }

                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                        AlertDialog.Builder editAD = new AlertDialog.Builder(Training2Activity.this);
                                        editAD.setTitle("")
                                                .setMessage(mac+"의 이벤트 등록이 완료되었습니다.\n" + "event : " + finalEvent + "\ntype : "+ type)
                                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent(Training2Activity.this,MenuActivity.class);
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


    public void training(View view) {
        String text = String.valueOf(button.getText());
        Calendar now = Calendar.getInstance();
        now.add(Calendar.HOUR_OF_DAY,9);
        if(text.equals("시작")) {
            onTime.add(now);
            textView.setText("기기를 작동해주세요");
            button.setText("중단");

            times++;
        }else {
            offTime.add(now);
            textView.setText("기기를 멈추고\n버튼이 회색이 되면\n다시 시작 버튼을 눌러주세요");
            button.setText("시작");
            button.setBackgroundColor(RED);
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    button.setBackgroundColor(LTGRAY);
                }
            }, 5000);

            if(times == 3){
                textView.setText("잠시만 기다려주세요");
                button.setVisibility(view.GONE);
                check(mac);
            }
        }
    }
}



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