package com.example.monitoring;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrainingResult2ButtonActivity extends AppCompatActivity {
    private LineChart mChart;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private  final String TAG = getClass().getSimpleName().trim();

    //setData()
    ArrayList<Entry> trafficValue = new ArrayList<>();
    ArrayList<Entry> commandValue = new ArrayList<>();
    String start, end;
    ArrayList<Integer> index;


    ArrayList<Entry> values = new ArrayList<>();
    ArrayList<Entry> valuesOn = new ArrayList<>();
    ArrayList<Entry> valuesOff = new ArrayList<>();

    String mac;
    int type;
    int eventT, eventC, event;
    int max;

    ArrayList<String> arr = new ArrayList();
    int size;
    private ArrayList<Integer> onTrafficGraph;
    private ArrayList<Integer> offTrafficGraph;
    private ArrayList<Integer> noTrafficGraph;
    private ArrayList<Integer> onCommandGraph;
    private ArrayList<Integer> offCommandGraph;
    private ArrayList<Integer> noCommandGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_result2_button);
        RadioButton type0 = findViewById(R.id.type0Button);
        RadioButton type1 = findViewById(R.id.type1Button);

        type0.setOnClickListener(type0ClickListener);
        type1.setOnClickListener(type1ClickListener);

        Intent intent = getIntent();

        mac = intent.getExtras().getString("mac");
        type = intent.getExtras().getInt("type");
        eventT = intent.getExtras().getInt("eventT");
        eventC = intent.getExtras().getInt("eventC");
        //start = intent.getExtras().getString("start");
        //end = intent.getExtras().getString("end");
        //index = intent.getExtras().getIntegerArrayList("index");

        onTrafficGraph = intent.getExtras().getIntegerArrayList("onTrafficGraph");
        offTrafficGraph = intent.getExtras().getIntegerArrayList("offTrafficGraph");
        noTrafficGraph = intent.getExtras().getIntegerArrayList("noTrafficGraph");
        onCommandGraph = intent.getExtras().getIntegerArrayList("onCommandGraph");
        offCommandGraph = intent.getExtras().getIntegerArrayList("offCommandGraph");
        noCommandGraph = intent.getExtras().getIntegerArrayList("noCommandGraph");
        arr = intent.getExtras().getStringArrayList("arr");
        size = intent.getExtras().getInt("size");


        Log.d(TAG,"onCommandGraph" + onCommandGraph);
        Log.d(TAG,"offCommandGraph" + offCommandGraph);
        Log.d(TAG,"noCommandGraph" + noCommandGraph);
        Log.d(TAG, "size"+size);


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
        setData2();
    }

    private void setData2() {
        mChart.getXAxis().setValueFormatter(new ChartXValueFormatter(arr));
        Log.d(TAG, "set2 ");

        if (type==0) {
            valuesOn.clear();
            valuesOff.clear();
            values.clear();
            Log.d(TAG, "type 0 ");

            for(int i=size;i>=0;i--){
                values.add(new Entry(i,noTrafficGraph.get(i)));
                valuesOn.add(new Entry(i,onTrafficGraph.get(i)));
                valuesOff.add(new Entry(i,offTrafficGraph.get(i)));
            }
        }
        else if (type==1){
            valuesOn.clear();
            valuesOff.clear();
            values.clear();
            Log.d(TAG, "type 1 ");

            for(int i=size;i>=0;i--){
                values.add(new Entry(i,noCommandGraph.get(i)));
                valuesOn.add(new Entry(i,onCommandGraph.get(i)));
                valuesOff.add(new Entry(i,offCommandGraph.get(i)));
            }
        }

        Collections.reverse(values);
        Collections.reverse(valuesOn);
        Collections.reverse(valuesOff);

        Log.d(TAG, "on values "+ valuesOn);
        Log.d(TAG, "off values "+ valuesOff);
        Log.d(TAG, "no values "+ values);

        LineDataSet set1;
        LineDataSet set2;
        LineDataSet set3;


        Log.d(TAG, "else");
        set1 = new LineDataSet(valuesOff, "off");
        set1.setDrawIcons(false);
        set1.enableDashedLine(10f, 5f, 0f); // 그래프 쌍곡선
        set1.enableDashedHighlightLine(10f, 5f, 0f);    // 터치했을 때 나오는 주황색 라인 관련
        set1.setColor(Color.BLUE);   // 쌍곡선 색깔
        set1.setCircleColor(Color.BLUE);
        set1.setLineWidth(1f);
        set1.setCircleRadius(1f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setDrawFilled(false);
        set1.setHighlightEnabled(true);

        set2 = new LineDataSet(valuesOn, "on");
        set2.setDrawIcons(false);
        set2.enableDashedLine(10f, 5f, 0f); // 그래프 쌍곡선
        set2.enableDashedHighlightLine(10f, 5f, 0f);    // 터치했을 때 나오는 주황색 라인 관련
        set2.setColor(Color.RED);   // 쌍곡선 색깔
        set2.setCircleColor(Color.RED);
        set2.setLineWidth(1f);
        set2.setCircleRadius(1f);
        set2.setDrawCircleHole(false);
        set2.setValueTextSize(9f);
        set2.setDrawFilled(false);
        set2.setHighlightEnabled(true);

        set3 = new LineDataSet(values,"no");
        set3.setDrawIcons(false);
        set3.enableDashedLine(10f, 5f, 0f); // 그래프 쌍곡선
        set3.enableDashedHighlightLine(10f, 5f, 0f);    // 터치했을 때 나오는 주황색 라인 관련
        set3.setColor(Color.LTGRAY);   // 쌍곡선 색깔
        set3.setCircleColor(Color.LTGRAY);
        set3.setLineWidth(1f);
        set3.setCircleRadius(1f);
        set3.setDrawCircleHole(false);
        set3.setValueTextSize(9f);
        set3.setDrawFilled(false);
        set3.setHighlightEnabled(true);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        dataSets.add(set2);
        dataSets.add(set3);
        LineData data = new LineData(dataSets);
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

                            //int x = values.size();
                            for(Integer index : index){
                                Log.d(TAG,  "Index " + index.intValue());
                                valuesOn.add(values.get(index.intValue()));
                                values.set(index.intValue(), new Entry(index.intValue(),0));
                            }
                            Collections.reverse(valuesOn);

                            int flag = 1;
                            for (int x = 0;x<values.size();x++){
                                for (Integer index: index){
                                    if (x== index){
                                        flag = 0;
                                        break;
                                    }
                                }
                                if (flag == 1){
                                    valuesOn.add(x,new Entry(x,0));
                                }
                                flag = 1;
                            }



                            //Collections.reverse(values);
                            Log.d(TAG, "on values "+ valuesOn);
                            Log.d(TAG, "off values "+ values);



                            mChart.getXAxis().setValueFormatter(new ChartXValueFormatter(arr));

                            LineDataSet set1;
                            LineDataSet set2;

                                Log.d(TAG, "else");
                                set1 = new LineDataSet(values, "off");
                                set1.setDrawIcons(false);
                                set1.enableDashedLine(10f, 5f, 0f); // 그래프 쌍곡선
                                set1.enableDashedHighlightLine(10f, 5f, 0f);    // 터치했을 때 나오는 주황색 라인 관련
                                set1.setColor(Color.BLUE);   // 쌍곡선 색깔
                                set1.setCircleColor(Color.BLUE);
                                set1.setLineWidth(1f);
                                set1.setCircleRadius(1f);
                                set1.setDrawCircleHole(false);
                                set1.setValueTextSize(9f);
                                set1.setDrawFilled(false);
                                set1.setHighlightEnabled(true);

                                set2 = new LineDataSet(valuesOn, "on");
                                set2.setDrawIcons(false);
                                set2.enableDashedLine(10f, 5f, 0f); // 그래프 쌍곡선
                                set2.enableDashedHighlightLine(10f, 5f, 0f);    // 터치했을 때 나오는 주황색 라인 관련
                                set2.setColor(Color.RED);   // 쌍곡선 색깔
                                set2.setCircleColor(Color.RED);
                                set2.setLineWidth(1f);
                                set2.setCircleRadius(1f);
                                set2.setDrawCircleHole(false);
                                set2.setValueTextSize(9f);
                                set2.setDrawFilled(false);
                                set2.setHighlightEnabled(true);

                                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                                dataSets.add(set1);
                                dataSets.add(set2);
                                LineData data = new LineData(dataSets);
                                mChart.invalidate();    // 이거 없으면 터치해야 그래프 나옴
                                mChart.setData(data);


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
        Intent intent = new Intent(TrainingResult2ButtonActivity.this,MenuActivity.class);
        startActivity(intent);
    }

    RadioButton.OnClickListener type0ClickListener = new RadioButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            type = 0;
            event = eventT;
            Log.d(TAG, "type "+ type+ "event " + event);
            setData2();
        }
    };


    RadioButton.OnClickListener type1ClickListener = new RadioButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            type = 1;
            event = eventC;
            Log.d(TAG, "type "+ type+ "event " + event);
            setData2();
        }
    };

}
