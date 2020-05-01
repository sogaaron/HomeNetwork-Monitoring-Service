package com.example.monitoring;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrainingResultBarActivity extends AppCompatActivity {
    private BarChart mChart;
    private  final String TAG = getClass().getSimpleName().trim();
    ArrayList<BarEntry> trafficValue = new ArrayList<>();
    ArrayList<BarEntry> commandValue = new ArrayList<>();

    ArrayList<BarEntry> values = new ArrayList<>();
    ArrayList<BarEntry> valuesOn = new ArrayList<>();

    ArrayList<String> arr = new ArrayList();
    String[] items;

    String mac;
    int type;
    int eventT, eventC, event;
    String start, end;

    int max;

    ArrayList<Integer> index;


    int gap = 60;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList mins = new ArrayList();
    ArrayAdapter adt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_result_bar);
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
        index = intent.getExtras().getIntegerArrayList("index");

        Log.d(TAG,"index" + index);

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
                                trafficValue.add(new BarEntry(--arrSize, traffic));  // Entry 1 부터 순서대로 그래프에 출력되는거라서 반대로 입력
                                commandValue.add(new BarEntry(arrSize, command));
                            }
                            if (type==0) {
                                //values.clear();
                                valuesOn.clear();
                                values = trafficValue;
                            }
                            else {
                                //values.clear();
                                valuesOn.clear();
                                values = commandValue;
                            }
                            Log.d(TAG, "values "+ values);
                            Collections.reverse(arr);
                            Collections.reverse(values);    // 1부터 즉, 순서대로 되도록 뒤집음, 안 그러면 에러남

                            for(Integer index : index){
                                Log.d(TAG,  "Index " + index.intValue());
                                valuesOn.add(values.get(index.intValue()));
                                values.remove(index.intValue());
                            }

                            Collections.reverse(valuesOn);
                            Log.d(TAG, "on values "+ valuesOn);
                            Log.d(TAG, "off values "+ values);



                            mChart.getXAxis().setValueFormatter(new ChartXValueFormatter(arr));

                            BarDataSet set1;
                            BarDataSet set2;

                            Log.d(TAG, "else");
                            set1 = new BarDataSet(values, "미작동");
                            set1.setDrawIcons(false);
                            set1.setColor(Color.BLUE);   // 쌍곡선 색깔
                            set1.setValueTextSize(9f);
                            set1.setHighlightEnabled(true);

                            set2 = new BarDataSet(valuesOn, "작동");
                            set2.setDrawIcons(false);
                            set2.setColor(Color.RED);   // 쌍곡선 색깔
                            set2.setValueTextSize(9f);
                            set2.setHighlightEnabled(true);
/*
                                if (Utils.getSDKInt() >= 18) {
                                    Drawable drawable = ContextCompat.getDrawable(TrainingResultActivity.this, R.drawable.fade_blue);
                                    set1.setFillDrawable(drawable);
                                    set2.setFillColor(Color.RED);
                                } else {
                                    set1.setFillColor(Color.DKGRAY);
                                    set2.setFillColor(Color.RED);
                                }

 */
                            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
                            dataSets.add(set1);
                            dataSets.add(set2);
                            BarData data = new BarData(dataSets);
                            mChart.invalidate();    // 이거 없으면 터치해야 그래프 나옴
                            mChart.setData(data);
                            //}

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
        Intent intent = new Intent(TrainingResultBarActivity.this,MenuActivity.class);
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
