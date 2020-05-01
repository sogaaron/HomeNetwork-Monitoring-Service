package com.example.monitoring;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import static java.lang.Thread.sleep;


public class Training2ButtonActivity extends AppCompatActivity {
    TextView textView;
    Button onButton, offButton, finishButton;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String TAG = "TrainingActivity";
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    ArrayList<Calendar> onStartTime = new ArrayList<>();
    ArrayList<Calendar> onEndTime = new ArrayList<>();

    ArrayList<Calendar> offStartTime = new ArrayList<>();
    ArrayList<Calendar> offEndTime = new ArrayList<>();



    //traffic
    ArrayList<Integer> onTraffic = new ArrayList<>();
    ArrayList<Integer> offTraffic = new ArrayList<>();
    ArrayList<Integer> onTrafficGraph = new ArrayList<>();
    ArrayList<Integer> offTrafficGraph = new ArrayList<>();
    ArrayList<Integer> allTraffic = new ArrayList<>();
    ArrayList<Integer> noTrafficGraph = new ArrayList<>();

    //command
    ArrayList<Integer> onCommand = new ArrayList<>();
    ArrayList<Integer> offCommand = new ArrayList<>();
    ArrayList<Integer> onCommandGraph = new ArrayList<>();
    ArrayList<Integer> offCommandGraph = new ArrayList<>();
    ArrayList<Integer> allCommand = new ArrayList<>();
    ArrayList<Integer> noCommandGraph = new ArrayList<>();

    ArrayList<String> arr = new ArrayList();



    String start, end; //트레이닝 시작, 끝 - 디비에서 가져올 때 사용

    int onSumTraffic, offSumTraffic = 0;
    int eventTraffic = 0;

    int onSumCommand, offSumCommand = 0;
    int eventCommand = 0;

    int finalEvent = 0;

    int onTimes = 0;
    int offTimes = 0;
    String mac;
    int type = 0;

    int size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training2_button);

        textView = findViewById(R.id.textViewTraining);
        onButton = findViewById(R.id.onButtonT);
        offButton = findViewById(R.id.offButtonT);
        finishButton = findViewById(R.id.finishButton);

        Intent intent = getIntent();

        mac = intent.getExtras().getString("mac");




//        check(mac);
    }

    private void check(final String mac) {
        Log.d(TAG, "MAC : "+mac);
        final int onsize = onStartTime.size();
        int offsize = offStartTime.size();

        db.collection("traffics")
                //        .whereEqualTo("nickname", selectedNickname)
                .orderBy("time", Query.Direction.DESCENDING)
                .whereGreaterThanOrEqualTo("time",start)
                .whereLessThanOrEqualTo("time", end)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            size = task.getResult().size()-1;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try {
                                    List ls = (List)document.getData().get(mac);
                                    int traffic = Integer.parseInt(ls.get(0).toString());
                                    int command = Integer.parseInt(ls.get(1).toString());
                                    String time = document.getString("time");

                                    arr.add(time.substring(14));

                                    Calendar cal = Calendar.getInstance();
                                    try {
                                        Date date = dateFormat.parse(time);
                                        cal.setTime(date);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    //allTraffic.add(traffic);
                                    //allCommand.add(command);

                                    //time
                                    /*
                                    Log.d(TAG, "0. " + onTime.get(0).getTime() + "  ~ "+ cal.getTime() + "  ~ " + offTime.get(0).getTime());
                                    Log.d(TAG, String.valueOf(cal.after(onTime.get(0)) && cal.before(offTime.get(0))));

                                    Log.d(TAG, "1. " + onTime.get(1).getTime() + "  ~ "+ cal.getTime() + "  ~ " + offTime.get(1).getTime());
                                    Log.d(TAG, String.valueOf(cal.after(onTime.get(1)) && cal.before(offTime.get(1))));

                                    Log.d(TAG, "2. " + onTime.get(2).getTime() + "  ~ "+ cal.getTime() + "  ~ " +offTime.get(2).getTime());
                                    Log.d(TAG, String.valueOf(cal.after(onTime.get(2)) && cal.before(offTime.get(2))));
                                     */

                                    if((cal.after(onStartTime.get(0)) && cal.before(onEndTime.get(0)))|| (cal.after(onStartTime.get(1)) && cal.before(onEndTime.get(1))) || (cal.after(onStartTime.get(2)) && cal.before(onEndTime.get(2)))){
                                        //작동할 때의 traffic
                                        onTraffic.add(traffic);
                                        onCommand.add(command);
                                        Log.d(TAG,"onCommand" + onCommand);

                                        onTrafficGraph.add(traffic);
                                        onCommandGraph.add(command);

                                        offTrafficGraph.add(0);
                                        offCommandGraph.add(0);
                                        noTrafficGraph.add(0);
                                        noCommandGraph.add(0);

                                        //index.add(arrSize);
                                    }else if ((cal.after(offStartTime.get(0)) && cal.before(offEndTime.get(0)))|| (cal.after(offStartTime.get(1)) && cal.before(offEndTime.get(1))) || (cal.after(offStartTime.get(2)) && cal.before(offEndTime.get(2)))){
                                        offTraffic.add(traffic);
                                        offCommand.add(command);
                                        Log.d(TAG,"offCommand" + offCommand);

                                        offTrafficGraph.add(traffic);
                                        offCommandGraph.add(command);

                                        onTrafficGraph.add(0);
                                        onCommandGraph.add(0);
                                        noTrafficGraph.add(0);
                                        noCommandGraph.add(0);
                                    }else{
                                        noTrafficGraph.add(0);
                                        noCommandGraph.add(0);

                                        onTrafficGraph.add(0);
                                        onCommandGraph.add(0);
                                        offTrafficGraph.add(0);
                                        offCommandGraph.add(0);
                                    }



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
                        double offAverageCommand = offSumCommand / offCommand.size();

                        eventTraffic = (int) ((onAverageTraffic + offAverageTraffic) / 2);
                        eventCommand = (int) ((onAverageCommand + offAverageCommand) / 2);

                        Log.d(TAG,"eventTraffic : " + eventTraffic + "  eventCommand" + eventCommand);
                        //average

                        if (onAverageTraffic-offAverageTraffic>1000){
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
                                        AlertDialog.Builder editAD = new AlertDialog.Builder(Training2ButtonActivity.this);
                                        editAD.setTitle("")
                                                .setMessage(mac+"의 이벤트 등록이 완료되었습니다.\n" + "event : " + finalEvent + "\ntype : "+ type)
                                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent(Training2ButtonActivity.this,MenuActivity.class);
                                                        startActivity(intent);
                                                    }
                                                })
                                                .setNegativeButton("결과", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent(Training2ButtonActivity.this,TrainingResult2ButtonActivity.class);
                                                        intent.putExtra("mac", mac);
                                                        intent.putExtra("type",type);
                                                        intent.putExtra("eventT",eventTraffic);
                                                        intent.putExtra("eventC", eventCommand);
                                                        intent.putExtra("start", start);
                                                        intent.putExtra("end", end);
                                                        //intent.putExtra("index", index);
                                                        intent.putExtra("onTrafficGraph", onTrafficGraph);
                                                        intent.putExtra("offTrafficGraph", offTrafficGraph);
                                                        intent.putExtra("noTrafficGraph", noTrafficGraph);
                                                        intent.putExtra("onCommandGraph", onCommandGraph);
                                                        intent.putExtra("offCommandGraph", offCommandGraph);
                                                        intent.putExtra("noCommandGraph", noCommandGraph);
                                                        intent.putExtra("arr", arr);
                                                        intent.putExtra("size", size);

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

    public void onTraining(View view) {
        String text = String.valueOf(onButton.getText());
        Calendar now = Calendar.getInstance();
        now.add(Calendar.HOUR_OF_DAY,9);

        if (text.equals("작동 시작")){
            if (onTimes==0) {
                Date date = now.getTime();
                start = dateFormat.format(date);
                Log.d(TAG, "start" + start);
            }
            onStartTime.add(now);
            onButton.setText("작동 끝");
            onButton.setBackgroundColor(Color.DKGRAY);
            offButton.setEnabled(false);
            finishButton.setEnabled(false);
        }else if (text.equals("작동 끝")){
            onButton.setText("작동 시작");
            onButton.setBackgroundColor(LTGRAY);
            onButton.setEnabled(false);

            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            now = Calendar.getInstance();
            now.add(Calendar.HOUR_OF_DAY,9);
            onEndTime.add(now);


            if (offTimes<3)
                offButton.setEnabled(true);
            finishButton.setEnabled(true);
            onTimes++;
        }
    }

    public void offTraining(View view) {
        String text = String.valueOf(offButton.getText());
        Calendar now = Calendar.getInstance();
        now.add(Calendar.HOUR_OF_DAY,9);

        if (text.equals("미작동 시작")){
            offStartTime.add(now);
            offButton.setText("미작동 끝");
            offButton.setBackgroundColor(Color.DKGRAY);
            onButton.setEnabled(false);
            finishButton.setEnabled(false);
        }else if (text.equals("미작동 끝")){
            offButton.setText("미작동 시작");
            offButton.setBackgroundColor(LTGRAY);
            offButton.setEnabled(false);

            try {
                sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            now = Calendar.getInstance();
            now.add(Calendar.HOUR_OF_DAY,9);
            offEndTime.add(now);

            if (onTimes<3)
                onButton.setEnabled(true);
            if (offTimes==2) {
                Date date = now.getTime();
                end = dateFormat.format(date);
                Log.d(TAG, "end" + end);
            }
            finishButton.setEnabled(true);
            offTimes++;
        }
    }

    public void finishAndShowResult(View view) {
        if (onTimes<3 || offTimes<3){
            Toast.makeText(getApplicationContext(),"트레이닝이 더 필요합니다.",Toast.LENGTH_SHORT).show();
        }else{
            textView.setText("잠시만 기다려주세요");

            onButton.setVisibility(view.GONE);
            offButton.setVisibility(view.GONE);
            finishButton.setVisibility(view.GONE);
/*
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR_OF_DAY,9);
            Date date = cal.getTime();
            end = dateFormat.format(date);
            Log.d(TAG, "end" + end);
*/
            check(mac);
        }
    }
}
