package com.example.monitoring;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

public class TestGraphActivity extends AppCompatActivity implements View.OnTouchListener, OnChartGestureListener {

    TextView textView;
    float oldXvalue;
    float oldYvalue;

    private LineChart mChart;
    private  final String TAG = getClass().getSimpleName().trim();
    ArrayList<Entry> values = new ArrayList<>();

    ArrayList<String> arr = new ArrayList();
    ArrayList<String> deviceList = new ArrayList();
    ArrayList<String> nickList = new ArrayList();
    ArrayList<Integer> eventList = new ArrayList<>();
    String[] items;
    String mac,nick;
    int event;
    int index;
    int gap = 60;
    int max;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList mins = new ArrayList();
    ArrayAdapter adt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_graph);

        // 텍스트 드래그
        textView = (TextView)findViewById(R.id.movemove);
        textView.setOnTouchListener(this);

        //

        db.collection("gabriel")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String mac = document.getString("mac");
                                Log.i("document", String.valueOf(document.get("normal")));
                                //String getevent = String.valueOf(document.get("normal"));
                                //int event = Integer.parseInt(getevent);
                                int event = document.getLong("normal").intValue();
                                String nick = document.getString("nickname");
                                deviceList.add(mac);
                                eventList.add(event);
                                nickList.add(nick);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        Log.e("ddddd","ddddddddddddddddddd");
                        mac = deviceList.get(0);
                        event = eventList.get(0);
                        nick = nickList.get(0);
                        Log.e("nick: ",nick);

                        new Thread(new Runnable() {     // renderData 함수 멀티 쓰레드로 실행
                            @Override public void run() { // TODO Auto-generated method stub
                                renderData();
                            }
                        }).start();
                    }
                });

        mChart = findViewById(R.id.chart);
        mChart.setOnChartGestureListener(this);
        mChart.setTouchEnabled(true);
        mChart.setPinchZoom(true);
        mChart.setNoDataText("waiting...");
        MyMarkerView mv = new MyMarkerView(getApplicationContext(), R.layout.custom_marker_view);
        mv.setChartView(mChart);
        mChart.setMarker(mv);


        lastMins();         // 그래프에서 시간 정하는 기능

    }

    public void lastMins(){
        mins.add(5); mins.add(10); mins.add(30); mins.add(60);
        Spinner sp = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter adt = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,mins);
        sp.setAdapter(adt);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0: gap = 60;
                        break;
                    case 1: gap = 120;
                        break;
                    case 2: gap = 360;
                        break;
                    case 3: gap = 720;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void renderData() {
//        LimitLine llXAxis = new LimitLine(10f, "Index 10");
//        llXAxis.setLineWidth(4f);
//        llXAxis.enableDashedLine(10f, 10f, 0f);
//        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
//        llXAxis.setTextSize(10f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);   // 그래프 안 y축 여러개
//        xAxis.setAxisMaximum(10f);
        xAxis.setAxisMinimum(0f);
        xAxis.setDrawLimitLinesBehindData(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);  // x축값 밑으로
        xAxis.setGranularityEnabled(true);  // 값 중복 제거
//        xAxis.setValueFormatter(new ChartXValueFormatter(arr));



        LimitLine ll1 = new LimitLine(215f, "Maximum Limit");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);

//        LimitLine ll2 = new LimitLine(70f, "Minimum Limit");
//        ll2.setLineWidth(4f);
//        ll2.enableDashedLine(10f, 10f, 0f);
//        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
//        ll2.setTextSize(10f);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
//        leftAxis.addLimitLine(ll1);
//        leftAxis.addLimitLine(ll2);
//        leftAxis.setAxisMaximum(350f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);    // 그래프 안 x축 여러개
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawLimitLinesBehindData(false);

        mChart.getAxisRight().setEnabled(false);
//        setData();
        Timer timer = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                mChart.notifyDataSetChanged();
                setData(mac);
            }
        };
        timer.schedule(tt,100,2000);    // FIrestore 에 어짜피 5초마다 한번 입력되니깐
    }

    private void setData(final String mac) {
        db.collection("traffics").orderBy("time", Query.Direction.DESCENDING).limit(gap)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int arrSize = task.getResult().size();
                            arr.clear();    // 이거 안하면 그래프 이전 것과 겹침
                            values.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                int traffic = document.getLong(mac).intValue();
                                String time = document.getString("time");
                                //Log.d("DATE", "time: " + time);
                                //Log.d("PUCK", "7c5c8d7drrr_traffic: " + traffic);
                                arr.add(time.substring(11));
                                values.add(new Entry(--arrSize, traffic));  // Entry 1 부터 순서대로 그래프에 출력되는거라서 반대로 입력
//                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            Collections.reverse(arr);
                            Collections.reverse(values);    // 1부터 즉, 순서대로 되도록 뒤집음, 안 그러면 에러남

                            mChart.getXAxis().setValueFormatter(new ChartXValueFormatter(arr));

                            LineDataSet set1;
                            if (mChart.getData() != null &&
                                    mChart.getData().getDataSetCount() > 0) {

                                set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
                                set1.setValues(values);
                                set1.setLabel(nick);

//                                set1.setLabel(mac);
                                mChart.getData().notifyDataChanged();
                                mChart.notifyDataSetChanged();
                                mChart.invalidate();    // 이거 없으면 터치해야 그래프 나옴

                            } else {

                                set1 = new LineDataSet(values, nick);
                                set1.setDrawIcons(false);
                                set1.enableDashedLine(10f, 5f, 0f); // 그래프 쌍곡선
                                set1.enableDashedHighlightLine(10f, 5f, 0f);    // 터치했을 때 나오는 주황색 라인 관련
                                set1.setColor(Color.DKGRAY);   // 쌍곡선 색깔
                                set1.setCircleColor(Color.DKGRAY);
                                set1.setLineWidth(1f);
                                set1.setCircleRadius(1f);
                                set1.setDrawCircleHole(false);
                                set1.setValueTextSize(9f);
                                set1.setDrawFilled(true);
//                                set1.setFormLineWidth(1f);
//                                set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
//                                set1.setFormSize(15.f);
//                                set1.setLabel(nick);
                                set1.setHighLightColor(Color.BLUE);

                                if (Utils.getSDKInt() >= 18) {
                                    Drawable drawable = ContextCompat.getDrawable(TestGraphActivity.this, R.drawable.fade_blue);
                                    set1.setFillDrawable(drawable);
                                } else {
                                    set1.setFillColor(Color.DKGRAY);
                                }


                                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                                dataSets.add(set1);
                                LineData data = new LineData(dataSets);
                                mChart.invalidate();    // 이거 없으면 터치해야 그래프 나옴
                                mChart.setData(data);

                            }

                            LimitLine eventline = new LimitLine(event, "EVENT");
                            eventline.setLineWidth(4f);
                            eventline.enableDashedLine(10f, 10f, 0f);
                            eventline.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                            eventline.setTextSize(10f);
                            YAxis leftAxis = mChart.getAxisLeft();
//                            int max;
                            if((int)mChart.getYMax() > event)
                                max = (int)mChart.getYMax();
                            else
                                max = event;

                            leftAxis.setAxisMaximum((int)(max*1.2));
                            leftAxis.removeAllLimitLines();
                            leftAxis.addLimitLine(eventline);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void showList(View view) {           // 'BUTTON' 버튼 눌럿을 떄
        items = new String[nickList.size()];
        items = nickList.toArray(items);
//        items = new String[deviceList.size()];
//        items = deviceList.toArray(items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("디바이스 리스트");

        builder.setItems(items, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int pos)
            {
                Toast.makeText(getApplicationContext(),items[pos],Toast.LENGTH_LONG).show();
                nick = items[pos];
//                mac = items[pos];

                // ArrayList Value index 확인
                index = nickList.indexOf(nick);
//                index = deviceList.indexOf(mac);
                System.out.println("six : index " + index + "\n");
                event = eventList.get(index);
                mac = deviceList.get(index);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public boolean onTouch(final View v, final MotionEvent eve) {


//        new Thread(new Runnable() {     // renderData 함수 멀티 쓰레드로 실행
//            @Override public void run() { // TODO Auto-generated method stub
        int width = ((ViewGroup) v.getParent()).getWidth() - v.getWidth();
        int height = ((ViewGroup) v.getParent()).getHeight() - v.getHeight();   // view frame 부분을 제외한 화면 세로 크기
        Log.e("widtheight:",width+" , "+height);
//                height -= 50;
//                Float f = new Float(height);
        Log.e("max:",max+"");
        Log.e("event:",event+"");


        if (eve.getAction() == MotionEvent.ACTION_DOWN) {
            oldXvalue = (int)eve.getX();
            oldYvalue = (int)eve.getY();
            Log.i("Tag1?", "Action Down X" + (int)eve.getX() + "," + (int)eve.getY());
            Log.i("Tag1", "Action Down rX " + (int)eve.getRawX() + "," + (int)eve.getRawY());
        } else if (eve.getAction() == MotionEvent.ACTION_MOVE) {
            v.setX((int)eve.getRawX() - oldXvalue);
            v.setY((int)eve.getRawY() - (oldYvalue + v.getHeight()));
            Log.i("Tag2", "Action Move " + (int)v.getX() + "," + (int)v.getY()+"," + v.getHeight());
            Log.i("Tag2", "Action Move22: " + (int)eve.getRawX() + "," + (int)eve.getRawY());
//                    event = 700 - (int)v.getY();
//                    v.setY(event);
            event = (int)(((height - v.getY())/height)*(int)(max*1.2));

            LimitLine eventline = new LimitLine(event, "Limit");
            eventline.setLineWidth(4f);
            eventline.enableDashedLine(10f, 10f, 0f);
            eventline.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            eventline.setTextSize(10f);
            YAxis leftAxis = mChart.getAxisLeft();
//                    int max;
            if((int)mChart.getYMax() > event)
                max = (int)mChart.getYMax();
            else
                max = event;

            leftAxis.setAxisMaximum((int)(max*1.2));
            leftAxis.removeAllLimitLines();
            leftAxis.addLimitLine(eventline);
            Log.e("limit move", event+"");
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.invalidate();

        } else if (eve.getAction() == MotionEvent.ACTION_UP) {
            Log.i("Tag3", "Action up " + (int)v.getX() + "," + (int)v.getY());

            if (v.getX() > width && v.getY() > height) {        // move frame 이 화면 넘어가지 않도록
                v.setX(width);
                v.setY(height);
            } else if (v.getX() < 0 && v.getY() > height) {
                v.setX(0);
                v.setY(height);
            } else if (v.getX() > width && v.getY() < 0) {
                v.setX(width);
                v.setY(0);
            } else if (v.getX() < 0 && v.getY() < 0) {
                v.setX(0);
                v.setY(0);
            } else if (v.getX() < 0 || v.getX() > width) {
                if (v.getX() < 0) {
                    v.setX(0);
                    v.setY((int)eve.getRawY() - oldYvalue - v.getHeight());
                } else {
                    v.setX(width);
                    v.setY((int)eve.getRawY() - oldYvalue - v.getHeight());
                }
            } else if (v.getY() < 0 || v.getY() > height) {
                if (v.getY() < 0) {
                    v.setX((int)eve.getRawX() - oldXvalue);
                    v.setY(0);
                } else {
                    v.setX((int)eve.getRawX() - oldXvalue);
                    v.setY(height);
                }
            }


            Log.i("Tag3", "Action up 22: " + (int)v.getX() + "," + (int)v.getY());
//
////                    event = 700 - (int)v.getY();
            LimitLine eventline = new LimitLine(event, "Limit");
            eventline.setLineWidth(4f);
            eventline.enableDashedLine(10f, 10f, 0f);
            eventline.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            eventline.setTextSize(10f);
            YAxis leftAxis = mChart.getAxisLeft();
//                    int max;
            if((int)mChart.getYMax() > event)
                max = (int)mChart.getYMax();
            else
                max = event;

            leftAxis.setAxisMaximum((int)(max*1.2));
            leftAxis.removeAllLimitLines();
            leftAxis.addLimitLine(eventline);
            Log.e("limit up", event+"");
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.invalidate();

        }
//            }
//        }).start();

        return true;
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("처음 눌렀을 때", String.valueOf(getApplicationContext()));
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        float x = me.getX();
        float y = me.getY();
        Log.i("want", String.valueOf(y));
        Log.i("getViewPortHandler()", String.valueOf(mChart.getViewPortHandler()));
        Log.i("getCenter()", String.valueOf(mChart.getCenter()));
        Log.i("끝났을 때", String.valueOf(getApplicationContext()));

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("길게 눌렀을 때", String.valueOf(getApplicationContext()));

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("두번 눌렀을 때", String.valueOf(getApplicationContext()));

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("한번 눌렀을 때", String.valueOf(getApplicationContext()));

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i("플링 제스쳐?", String.valueOf(getApplicationContext()));

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

        Log.i("크기 조정", String.valueOf(getApplicationContext()));

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

        Log.i("차트 이동", String.valueOf(getApplicationContext()));

    }

    public void enterEvent(View view) {
        final EditText editText = new EditText(TestGraphActivity.this);
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if(keyCode == event.KEYCODE_ENTER)
                {
                    return true;
                }
                return false;
            }
        });

        AlertDialog.Builder editAD = new AlertDialog.Builder(TestGraphActivity.this);
        editAD.setTitle("")
                .setMessage("설정할 값을 입력하세요")
                .setView(editText)
                .setNegativeButton("취소", null)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            String eventinput = editText.getText().toString().trim().replace(" ","");
                            event = Integer.parseInt(eventinput);
                            Toast.makeText(getApplicationContext(),String.valueOf(event),Toast.LENGTH_SHORT).show();



                            //event = Integer.parseInt(eventinput);

                            eventList.set(index, event);
                            LimitLine eventline = new LimitLine(event, "EVENT");
                            eventline.setLineWidth(4f);
                            eventline.enableDashedLine(10f, 10f, 0f);
                            eventline.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                            eventline.setTextSize(10f);

                            YAxis leftAxis = mChart.getAxisLeft();
                            Log.e("yyyyy: ",mChart.getYMax()+"");
//                            int max;
                            if((int)mChart.getYMax() > event)
                                max = (int)mChart.getYMax();
                            else
                                max = event;
                            leftAxis.setAxisMaximum((int)(max*1.2));
                            leftAxis.removeAllLimitLines();
                            leftAxis.addLimitLine(eventline);

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
                                        }
                                    });

                        } catch(NumberFormatException e){
                            //if (eventinput.equals("")) {
                            //    Toast.makeText(getApplicationContext(), "값을 입력하세요.", Toast.LENGTH_SHORT).show();
                            //}else {
                            Toast.makeText(getApplicationContext(), "숫자를 입력하세요", Toast.LENGTH_LONG).show();
                            //}
                        }





                    }
                });
        AlertDialog alert = editAD.create();
        alert.show();
    }
}