package com.example.monitoring;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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

public class GraphActivity extends AppCompatActivity {

    private LineChart mChart;
    private  final String TAG = getClass().getSimpleName().trim();
    ArrayList<Entry> values = new ArrayList<>();

    ArrayList<String> arr = new ArrayList();
    ArrayList<String> deviceList = new ArrayList();
    String[] items;
    String mac;
    int gap = 60;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList mins = new ArrayList();
    ArrayAdapter adt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        db.collection("gabriel")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String mac = document.getString("mac");
                                deviceList.add(mac);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        mac = deviceList.get(0);

                        new Thread(new Runnable() {     // renderData 함수 멀티 쓰레드로 실행
                            @Override public void run() { // TODO Auto-generated method stub
                                renderData();
                            }
                        }).start();
                    }
                });


        mChart = findViewById(R.id.chart);
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
        xAxis.enableGridDashedLine(10f, 10f, 0f);
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

        LimitLine ll2 = new LimitLine(70f, "Minimum Limit");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
//        leftAxis.setAxisMaximum(350f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
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
                                arr.add(time.substring(14));
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
                                set1.setLabel(mac);
                                mChart.getData().notifyDataChanged();
                                mChart.notifyDataSetChanged();
                                mChart.invalidate();    // 이거 없으면 터치해야 그래프 나옴

                            } else {

                                set1 = new LineDataSet(values, mac);
                                set1.setDrawIcons(false);
                                set1.enableDashedLine(10f, 5f, 0f);
                                set1.enableDashedHighlightLine(10f, 5f, 0f);
                                set1.setColor(Color.DKGRAY);
                                set1.setCircleColor(Color.DKGRAY);
                                set1.setLineWidth(1f);
                                set1.setCircleRadius(1f);
                                set1.setDrawCircleHole(false);
                                set1.setValueTextSize(9f);
                                set1.setDrawFilled(true);
                                set1.setFormLineWidth(1f);
                                set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
                                set1.setFormSize(15.f);

                                if (Utils.getSDKInt() >= 18) {
                                    Drawable drawable = ContextCompat.getDrawable(GraphActivity.this, R.drawable.fade_blue);
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
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void showList(View view) {           // 'BUTTON' 버튼 눌럿을 떄
        items = new String[deviceList.size()];
        items = deviceList.toArray(items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("디바이스 리스트");

        builder.setItems(items, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int pos)
            {
                Toast.makeText(getApplicationContext(),items[pos],Toast.LENGTH_LONG).show();
                mac = items[pos];
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}