package com.example.monitoring;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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


public class Training2Activity extends AppCompatActivity {
    TextView textView;
    Button onButton, offButton, finishButton;

    final Handler handler = new Handler();
    int i=0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String TAG = "TrainingActivity";
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    //ArrayList<Calendar> onTime = new ArrayList<>();
    //ArrayList<Calendar> offTime = new ArrayList<>();

    ArrayList<Calendar> onStartTime = new ArrayList<>();
    ArrayList<Calendar> onEndTime = new ArrayList<>();

    ArrayList<Calendar> offStartTime = new ArrayList<>();
    ArrayList<Calendar> offEndTime = new ArrayList<>();



    //traffic
    ArrayList<Integer> onTraffic = new ArrayList<>();
    ArrayList<Integer> offTraffic = new ArrayList<>();
    ArrayList<Integer> allTraffic = new ArrayList<>();
    ArrayList<Integer> noTraffic = new ArrayList<>();

    ArrayList<Integer> onTraffic_first = new ArrayList<>();
    ArrayList<Integer> onTraffic_second = new ArrayList<>();
    ArrayList<Integer> onTraffic_third = new ArrayList<>();
    ArrayList<Integer> offTraffic_first = new ArrayList<>();
    ArrayList<Integer> offTraffic_second = new ArrayList<>();
    ArrayList<Integer> offTraffic_third = new ArrayList<>();
    ArrayList<Integer> total_traffic = new ArrayList<>();


    //command
    ArrayList<Integer> onCommand = new ArrayList<>();
    ArrayList<Integer> offCommand = new ArrayList<>();
    ArrayList<Integer> allCommand = new ArrayList<>();
    ArrayList<Integer> noCommand = new ArrayList<>();

    ArrayList<Integer> onCommand_first = new ArrayList<>();
    ArrayList<Integer> onCommand_second = new ArrayList<>();
    ArrayList<Integer> onCommand_third = new ArrayList<>();
    ArrayList<Integer> offCommand_first = new ArrayList<>();
    ArrayList<Integer> offCommand_second = new ArrayList<>();
    ArrayList<Integer> offCommand_third = new ArrayList<>();
    ArrayList<Integer> total_command = new ArrayList<>();

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

    ArrayList<Integer> index = new ArrayList<>();
    ArrayList<Integer> index2 = new ArrayList<>();




    //Calendar[] onTime = new Calendar[5];
    //Calendar[] offTime = new Calendar[4];







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training2);

//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.HOUR_OF_DAY,9);
//        Date date = cal.getTime();
//        start = dateFormat.format(date);
//        Log.d(TAG, "start" + start);


        textView = findViewById(R.id.textViewTraining);
        onButton = findViewById(R.id.onButtonT);
        offButton = findViewById(R.id.offButtonT);
        offButton.setEnabled(false);
        finishButton = findViewById(R.id.finishButton);
        finishButton.setEnabled(false);

        Intent intent = getIntent();

        mac = intent.getExtras().getString("mac");




//        check(mac);
    }

    private void check(final String mac) {
        Log.d(TAG, "MAC : "+mac);
        final int onsize = onStartTime.size();
        int offsize = offStartTime.size();

        Date date = onStartTime.get(0).getTime();
        start = dateFormat.format(date);
        Date date22 = offEndTime.get(2).getTime();
        end = dateFormat.format(date22);

        for(Calendar c: onStartTime){
            Date d = c.getTime();
            String tmp = dateFormat.format(d);
            Log.e("a","start         "+tmp);
        }
        for(Calendar c: onEndTime){
            Date d = c.getTime();
            String tmp = dateFormat.format(d);
            Log.e("a","end         "+tmp);
        }

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
                            int arrSize = task.getResult().size()-1;
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

//                                    if((cal.after(onStartTime.get(0)) && cal.before(onEndTime.get(0)))|| (cal.after(onStartTime.get(1)) && cal.before(onEndTime.get(1))) || (cal.after(onStartTime.get(2)) && cal.before(onEndTime.get(2)))){
//                                        //작동할 때의 traffic
//                                        onTraffic.add(traffic); // onTraffic, onCommand 는 단지 이벤트값 계산하려는 용도, 그래프화면에 넘길 값들이 아님
//                                        onCommand.add(command);
//                                        Log.d(TAG,"onTraffic" + onTraffic);
//                                        index.add(arrSize);
//                                    }else if((cal.after(offStartTime.get(0)) && cal.before(offEndTime.get(0)))|| (cal.after(offStartTime.get(1)) && cal.before(offEndTime.get(1))) || (cal.after(offStartTime.get(2)) && cal.before(offEndTime.get(2)))){
//
//                                        offTraffic.add(traffic);
//                                        offCommand.add(command);
//                                        Log.d(TAG,"offTraffic" + offTraffic);
//                                        index2.add(arrSize);
//                                    }

                                    if(cal.after(onStartTime.get(0)) && cal.before(onEndTime.get(0))){
                                        onTraffic_first.add(traffic);
                                        onCommand_first.add(command);
                                    }
                                    else if(cal.after(onStartTime.get(1)) && cal.before(onEndTime.get(1))){
                                        onTraffic_second.add(traffic);
                                        onCommand_second.add(command);
                                    }
                                    else if(cal.after(onStartTime.get(2)) && cal.before(onEndTime.get(2))){
                                        onTraffic_third.add(traffic);
                                        onCommand_third.add(command);
                                    }
                                    else if(cal.after(offStartTime.get(0)) && cal.before(offEndTime.get(0))){
                                        offTraffic_first.add(traffic);
                                        offCommand_first.add(command);
                                    }
                                    else if(cal.after(offStartTime.get(1)) && cal.before(offEndTime.get(1))){
                                        offTraffic_second.add(traffic);
                                        offCommand_second.add(command);
                                    }
                                    else if(cal.after(offStartTime.get(2)) && cal.before(offEndTime.get(2))){
                                        offTraffic_third.add(traffic);
                                        offCommand_third.add(command);
                                    }
//                                    for (int i = 0; i<onsize; i++){
//                                        if(cal.after(onStartTime.get(i)) && cal.before(onEndTime.get(i))){
//
//                                        }
//                                    }

                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    //time
                                }catch (NullPointerException e){
                                    Log.e("err",e.getMessage());
                                }
                                arrSize--;
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }

//                        Collections.sort(onTraffic);
//                        Collections.sort(offTraffic);
//                        Collections.reverse(offTraffic);
//
//                        Collections.sort(onCommand);
//                        Collections.sort(offCommand);
//                        Collections.reverse(onCommand);
//
//                        onTraffic.remove(0);
//                        offTraffic.remove(0);
//
//                        onCommand.remove(0);
//                        offCommand.remove(0);

                        onTraffic.add(Collections.max(onTraffic_first));
                        onTraffic.add(Collections.max(onTraffic_second));
                        onTraffic.add(Collections.max(onTraffic_third));

                        onCommand.add(Collections.max(onCommand_first));
                        onCommand.add(Collections.max(onCommand_second));
                        onCommand.add(Collections.max(onCommand_third));

                        int sum = 0;
                        for(int tmp:offTraffic_first){
                            sum += tmp;
                        }
                        offTraffic.add(sum/offTraffic_first.size());

                        sum = 0;
                        for(int tmp:offTraffic_second){
                            sum += tmp;
                        }
                        offTraffic.add(sum/offTraffic_second.size());

                        sum = 0;
                        for(int tmp:offTraffic_third){
                            sum += tmp;
                        }
                        offTraffic.add(sum/offTraffic_third.size());

                        sum = 0;
                        for(int tmp:offCommand_first){
                            sum += tmp;
                        }
                        offCommand.add(sum/offCommand_first.size());

                        sum = 0;
                        for(int tmp:offCommand_second){
                            sum += tmp;
                        }
                        offCommand.add(sum/offCommand_second.size());

                        sum = 0;
                        for(int tmp:offCommand_third){
                            sum += tmp;
                        }
                        offCommand.add(sum/offCommand_third.size());

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

                        for(int w=0;w<3;w++){
                            total_traffic.add(onTraffic.get(w));
                            total_traffic.add(offTraffic.get(w));
                            total_command.add(onCommand.get(w));
                            total_command.add(offCommand.get(w));

                        }

                        //average
                        double onAverageTraffic = onSumTraffic / onTraffic.size();
                        double offAverageTraffic = offSumTraffic / offTraffic.size();

                        double onAverageCommand = onSumCommand / onCommand.size();
                        double offAverageCommand = offSumCommand / offCommand.size();

                        eventTraffic = (int) ((onAverageTraffic + offAverageTraffic) / 2);
                        eventCommand = (int) ((onAverageCommand + offAverageCommand) / 2);

                        Log.d(TAG,"eventTraffic : " + eventTraffic + "  eventCommand" + eventCommand);
                        //average

                        if (onAverageTraffic - offAverageTraffic > 1000){
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
                                                })
                                                .setNegativeButton("결과", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent(Training2Activity.this,TrainingResultActivity.class);
                                                        intent.putExtra("mac", mac);
                                                        intent.putExtra("type",type);
                                                        intent.putExtra("eventT",eventTraffic);
                                                        intent.putExtra("eventC", eventCommand);
                                                        intent.putExtra("start", start);
                                                        intent.putExtra("end", end);
                                                        intent.putExtra("total_traf", total_traffic);
                                                        intent.putExtra("total_com", total_command);

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

/*
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

                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.HOUR_OF_DAY,9);
                Date date = cal.getTime();
                end = dateFormat.format(date);
                Log.d(TAG, "end" + end);

                check(mac);
            }
        }
    }

 */

    public void onTraining(View view) {
        String text = String.valueOf(onButton.getText());

        if (text.equals("작동 시작")){
            Calendar now = Calendar.getInstance();
            now.add(Calendar.HOUR_OF_DAY,9);
            onStartTime.add(now);
            onButton.setText("작동 끝");
            onButton.setBackgroundColor(Color.DKGRAY);
            //onButton.setSelected(true);
            offButton.setEnabled(false);
//            finishButton.setEnabled(false);
        }else if (text.equals("작동 끝")){
            try {
                Thread.sleep(5000);
            }catch (Exception e){
            }
            Calendar now = Calendar.getInstance();
            now.add(Calendar.HOUR_OF_DAY,9);
            onEndTime.add(now);
            onButton.setText("작동 시작");
            onButton.setBackgroundColor(LTGRAY);
            //onButton.setSelected(false);
            if (offTimes<3)
                offButton.setEnabled(true);
            onButton.setEnabled(false);
//            finishButton.setEnabled(true);
            onTimes++;
        }
    }

    public void offTraining(View view) {
        String text = String.valueOf(offButton.getText());


        if (text.equals("미작동 시작")){
            Calendar now = Calendar.getInstance();
            now.add(Calendar.HOUR_OF_DAY,9);
            offStartTime.add(now);
            offButton.setText("미작동 끝");
            offButton.setBackgroundColor(Color.DKGRAY);
            //offButton.setSelected(true);
            onButton.setEnabled(false);
//            finishButton.setEnabled(false);
        }else if (text.equals("미작동 끝")){
            try {
                Thread.sleep(5000);
            }catch (Exception e){
            }
            Calendar now = Calendar.getInstance();
            now.add(Calendar.HOUR_OF_DAY,9);
            offEndTime.add(now);
            offButton.setText("미작동 시작");
            offButton.setBackgroundColor(LTGRAY);
            //offButton.setSelected(false);

            if (onTimes<3)
                onButton.setEnabled(true);
            else
                finishButton.setEnabled(true);

            offButton.setEnabled(false);
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

            check(mac);
        }
    }
}

