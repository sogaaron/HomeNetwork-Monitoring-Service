package com.example.monitoring;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SettingTimeListAdapter2 extends BaseAdapter {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Context context = null;
    LayoutInflater layoutInflater = null;
    private ArrayList<SettingTimeListViewItem> listViewItemList = null;
    private String TAG = "SettingTimeListAdapter2";
    String pposition;

    public SettingTimeListAdapter2(ArrayList<SettingTimeListViewItem> _oData) {
        listViewItemList = _oData;
    }

    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Context context = parent.getContext();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
            final SettingTimeListViewItem listViewItem = listViewItemList.get(position);
            //final Context context = parent.getContext();
            convertView = inflater.inflate(R.layout.listview_setting_time2, parent, false);

            TextView textView = convertView.findViewById(R.id.textViewTime2);
            textView.setText(listViewItem.getTime());

            Button deleteButton = (Button) convertView.findViewById(R.id.buttonDeleteMode);
            deleteButton.setOnClickListener(listViewItemList.get(position).onClickListener);

            convertView.setTag(position);
            Log.d(TAG, position + "???");


        /*
        final Context context = parent.getContext();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
            final SettingTimeListViewItem listViewItem = listViewItemList.get(position);
            //final Context context = parent.getContext();
            convertView = inflater.inflate(R.layout.listview_setting_time2, parent, false);

            TextView textView = convertView.findViewById(R.id.textViewTime2);
            Button button = convertView.findViewById(R.id.buttonDeleteMode);
            textView.setText(listViewItem.getTime());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View oParentView = (View)v.getParent(); // 부모의 View를 가져온다. 즉, 아이템 View임.
                    Log.d(TAG, "TAG" + oParentView.getTag());
                    Log.d(TAG, "Position" + position);

                    pposition = (String) oParentView.getTag();


                    String text=null;
                    TextView tv = oParentView.findViewById(R.id.textViewTime2);
                    text = (String) tv.getText();
                    Log.d(TAG, "Clicked!!!!!!!!!11"+text + position);

                    listViewItemList.remove(position);

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
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                    }

                                }
                                                                    //oAdapter.notifyDataSetChanged();
                            });
                }
            });
        }

         */
        }


        return convertView;



    }

/*
    public void addItem(String time, boolean flag) {
        SettingTimeListViewItem item= new SettingTimeListViewItem() ;

        item.setTime(time);
        item.setFlag(flag);
        //item.onClickListener = applicationContext;

        Log.e("qqqqqqqqqq",item.getTime() +"  "+ item.getFlag());
        listViewItemList.add(item);
    }

 */

}
