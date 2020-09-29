package com.example.monitoring;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class SettingTimeListActivity extends AppCompatActivity implements View.OnClickListener {
    //ArrayList<String> arrayList;
    SettingTimeListAdapter adapter = null;
    SettingTimeListAdapter2 adapter2 = null;

    ArrayList<String> notiTime = new ArrayList();
    ArrayList<Boolean> notiFlag = new ArrayList<>();
    ArrayList<SettingTimeListViewItem> oData = new ArrayList<>();
    ListView listview;




    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String TAG = "SettingTimeListActivity";
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    Button b2;
    int position;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_time_list);
        b2 = findViewById(R.id.buttonDelete);

        //arrayList = new ArrayList<>();

        db.collection("notificationTime")//.orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                /*

                                Timestamp timestampStart = document.getTimestamp("start");
                                Timestamp timestampEnd = document.getTimestamp("end");

                                 */
                                boolean flag = document.getBoolean("flag");
                                String time = document.getString("timetext");

                                //시간변환
                                /*
                                Date date1 = timestampStart.toDate();
                                Date date2 = timestampEnd.toDate();
                                Calendar cal1 = Calendar.getInstance();

                                Calendar cal2 = Calendar.getInstance();
                                cal1.setTime(date1);
                                cal2.setTime(date2);
                                cal1.add(Calendar.HOUR_OF_DAY,9);
                                cal2.add(Calendar.HOUR_OF_DAY,9);

                                String start = dateFormat.format(cal1.getTime());
                                String end = dateFormat.format(cal2.getTime());
                                Log.d("SettingTimeAcitivity", "show" + start + "  " + end);

                                 */
                                //adapter.addItem(time,flag);
                                //adapter2.addItem(time,flag);
                                notiTime.add(time);
                                notiFlag.add(flag);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            list();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void list() {
        for(int i = 0; i<notiTime.size(); i++){
            SettingTimeListViewItem item = new SettingTimeListViewItem();
            item.time = notiTime.get(i);
            item.flag = notiFlag.get(i);
            item.onClickListener = this;
            oData.add(item);
            Log.d(TAG, "add list" + i);
        }
        listview = findViewById(R.id.listviewTime);
        adapter = new SettingTimeListAdapter(oData);
        adapter2 = new SettingTimeListAdapter2(oData);
        listview.setAdapter(adapter);
        Log.d(TAG, "set adapter");
    }

    public void addMode(View view) {
        Intent intent = new Intent(SettingTimeListActivity.this,SettingTimeActivity.class);
        startActivity(intent);
    }

    public void deleteMode(View view) {
        ListView listview;
        listview = findViewById(R.id.listviewTime);
        if (b2.getText().equals("방해금지 모드 삭제하기")){
            listview.setAdapter(adapter2);
            b2.setText("리스트로 돌아가기");
        }else {
            listview.setAdapter(adapter);
            b2.setText("방해금지 모드 삭제하기");

        }
    }

    @Override
    public void onClick(View v) {
        View oParentView = (View)v.getParent(); // 부모의 View를 가져온다. 즉, 아이템 View임.
        View vv = (View) oParentView.getParent();
        TextView tv = (TextView) oParentView.findViewById(R.id.textViewTime2);
        position = (int) vv.getTag();
        Log.d(TAG, position + "why");

        String text=null;
        text = (String) tv.getText();

        db.collection("notificationTime")
                .whereEqualTo("timetext", text)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("notificationTime").document(document.getId()).delete();
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            oData.remove(position);
                            //int totalElements = oData.size();// arrayList의 요소의 갯수를 구한다.
                            //for (int index = 0; index < totalElements; index++) {
                                //System.out.println(oData.get(index));
                            //    Log.i("delete check", String.valueOf(oData.get(index)));
                            //}
                            adapter = new SettingTimeListAdapter(oData);
                            adapter2 = new SettingTimeListAdapter2(oData);
                            listview.setAdapter(adapter2);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                    //oAdapter.notifyDataSetChanged();
                });

    }
}
