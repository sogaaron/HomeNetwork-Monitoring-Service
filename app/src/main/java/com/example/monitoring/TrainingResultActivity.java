package com.example.monitoring;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;

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
import com.github.mikephil.charting.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TrainingResultActivity extends AppCompatActivity {
    private LineChart mChart;
    private  final String TAG = getClass().getSimpleName().trim();
    ArrayList<Entry> trafficValue = new ArrayList<>();
    ArrayList<Entry> commandValue = new ArrayList<>();

    ArrayList<Entry> values = new ArrayList<>();



    ArrayList<String> arr = new ArrayList();
    String[] items;

    String mac;
    int type;
    int eventT, eventC, event;
    String start, end;

    int max;

    ArrayList<Integer> index, index2;
    ArrayList<Integer> colors = new ArrayList<>();


    int gap = 60;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList mins = new ArrayList();
    ArrayAdapter adt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_result);
        RadioButton type0 = findViewById(R.id.type0Button);
        RadioButton type1 = findViewById(R.id.type1Button);

        type0.setOnClickListener(type0ClickListener);
        type1.setOnClickListener(type1ClickListener);

        Intent intent = getIntent();

        mac = intent.getExtras().getString("mac");
        type = intent.getExtras().getInt("type");
        eventT = intent.getExtras().getInt("eventT");
        eventC = intent.getExtras().getInt("eventC");
        start = intent.getExtras().getString("start");
        end = intent.getExtras().getString("end");
        index = intent.getExtras().getIntegerArrayList("index");    // 작동중일 때의 index 리스트
        index2 = intent.getExtras().getIntegerArrayList("index2");    // 미작동중일 때의 index 리스트


        if (type == 0){
            type0.setChecked(true);
            type0.setText("Type 0(recommend)");
            event = eventT;
        }
        else {
            type1.setChecked(true);
            type1.setText("Type 1(recommend)");
            event = eventC;
        }

        mChart = findViewById(R.id.chart);
        mChart.setTouchEnabled(true);
        mChart.setPinchZoom(true);
        mChart.setNoDataText("waiting...");
        MyMarkerView mv = new MyMarkerView(getApplicationContext(), R.layout.custom_marker_view);
        mv.setChartView(mChart);
        mChart.setMarker(mv);

        renderData();
    }

    private void renderData() {
        //        LimitLine llXAxis = new LimitLine(10f, "Index 10");
//        llXAxis.setLineWidth(4f);
//        llXAxis.enableDashedLine(10f, 10f, 0f);
//        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
//        llXAxis.setTextSize(10f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
//        xAxis.setAxisMaximum(10f);
        xAxis.setAxisMinimum(0f);
        xAxis.setDrawLimitLinesBehindData(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);  // x축값 밑으로
        xAxis.setGranularityEnabled(true);  // 값 중복 제거
//        xAxis.setValueFormatter(new ChartXValueFormatter(arr));



//        LimitLine eventline = new LimitLine(event, "EVENT");
//        eventline.setLineWidth(4f);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
//        leftAxis.addLimitLine(eventline);
//        leftAxis.setAxisMaximum(350f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawLimitLinesBehindData(false);

        mChart.getAxisRight().setEnabled(false);
        setData();
        Timer timer = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                mChart.notifyDataSetChanged();
                setData();
            }
        };
        timer.schedule(tt,100,2000);
    }

    private void setData() {
        db.collection("traffics").orderBy("time", Query.Direction.DESCENDING)
                .whereGreaterThanOrEqualTo("time", start)
                .whereLessThanOrEqualTo("time", end)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int arrSize = task.getResult().size();
                            arr.clear();    // 이거 안하면 그래프 이전 것과 겹침
                            trafficValue.clear();
                            commandValue.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                List ls = (List)document.getData().get(mac);
                                int traffic = Integer.parseInt(ls.get(0).toString());
                                int command = Integer.parseInt(ls.get(1).toString());
                                String time = document.getString("time");

                                arr.add(time.substring(14));

                                trafficValue.add(new Entry(--arrSize, traffic));  // Entry 1 부터 순서대로 그래프에 출력되는거라서 반대로 입력
                                commandValue.add(new Entry(arrSize, command));

                            }
                            if (type==0) {
                                //values.clear();
                                values = trafficValue;
                            }
                            else {
                                //values.clear();
                                values = commandValue;
                            }
                            Log.d(TAG, "values "+ values);
                            Collections.reverse(arr);
                            Collections.reverse(values);    // 1부터 즉, 순서대로 되도록 뒤집음, 안 그러면 에러남

                            mChart.getXAxis().setValueFormatter(new ChartXValueFormatter(arr)); // x축 내용 표시

                            LineDataSet set1, set2;
                            if (mChart.getData() != null &&
                                    mChart.getData().getDataSetCount() > 0) {
                                Log.d(TAG, "if");
                                set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
                                set1.setValues(values);
                                set1.setLabel(mac);

                                Log.e("ff","firstttttttttttttttttt  "+arrSize);

                                for(int num=0;num<task.getResult().size();num++){
                                    if(index.contains(num))
                                        colors.add(Color.RED);
                                    else if(index2.contains(num))
                                        colors.add(Color.BLUE);
                                    else
                                        colors.add(Color.BLACK);
                                    Log.e("ff","enddddddddddddddd");

                                }
                                set1.setCircleColors(colors);
//                                set1.setColors(colors);

                                mChart.getData().notifyDataChanged();
                                mChart.notifyDataSetChanged();
                                mChart.invalidate();    // 이거 없으면 터치해야 그래프 나옴
                            } else {
                                Log.d(TAG, "else");
                                set1 = new LineDataSet(values, mac);
                                set1.setDrawIcons(false);
//                                set1.enableDashedLine(10f, 5f, 0f); // 그래프 쌍곡선
                                set1.enableDashedHighlightLine(10f, 5f, 0f);    // 터치했을 때 나오는 주황색 라인 관련
                                set1.setColor(Color.DKGRAY);   // 쌍곡선 색깔

                                set1.setCircleColor(Color.DKGRAY);
                                set1.setLineWidth(3f);
                                set1.setCircleRadius(5f);
                                set1.setDrawCircleHole(false);
                                set1.setValueTextSize(9f);
                                set1.setDrawFilled(true);


//                                set1.setFormLineWidth(1f);
//                                set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
//                                set1.setFormSize(15.f);
//                                set1.setLabel(nick);
//                                set1.setHighLightColor(Color.BLUE);
                                set1.setHighlightEnabled(true);

                                if (Utils.getSDKInt() >= 18) {
                                    Drawable drawable = ContextCompat.getDrawable(TrainingResultActivity.this, R.drawable.fade_blue);
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

                            if((int)mChart.getYMax() < 10 && max < 10){
                                max = 10;
                            }

                            leftAxis.setAxisMaximum((int)(max*1.2));    //                            leftAxis.removeAllLimitLines();
                            leftAxis.removeAllLimitLines();
                            leftAxis.addLimitLine(eventline);

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void saveResult(View view) {

        db.collection("gabriel")
                .whereEqualTo("mac", mac)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("gabriel").document(document.getId()).update("normal", event);
                                db.collection("gabriel").document(document.getId()).update("type", type);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });
        Intent intent = new Intent(TrainingResultActivity.this,MenuActivity.class);
        startActivity(intent);

    }

    RadioButton.OnClickListener type0ClickListener = new RadioButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            type = 0;
            event = eventT;
            Log.d(TAG, "type "+ type+ "event " + event);
            setData();
            setData();
        }
    };

    RadioButton.OnClickListener type1ClickListener = new RadioButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            type = 1;
            event = eventC;
            Log.d(TAG, "type "+ type+ "event " + event);
            setData();
            setData();
        }
    };
}