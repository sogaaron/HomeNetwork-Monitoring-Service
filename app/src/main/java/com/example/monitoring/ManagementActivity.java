package com.example.monitoring;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ManagementActivity extends AppCompatActivity implements View.OnClickListener {
    private  final String TAG = getClass().getSimpleName().trim();
    private ListView m_oListView = null;
    ArrayList<String> deviceList = new ArrayList();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DeviceListAdapter oAdapter;
    ArrayList<DeviceListViewItem> oData = new ArrayList<>();
    String nick;
    String position;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);

        db.collection("gabriel")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String nickname = document.getString("nickname");
                                deviceList.add(nickname);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            list();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });







    }

    public void list(){
        for(int i = 0; i<deviceList.size(); i++){
            DeviceListViewItem oItem = new DeviceListViewItem();
            oItem.nickname = deviceList.get(i);
            oItem.onClickListener = this;
            oData.add(oItem);
        }


        m_oListView = (ListView)findViewById(R.id.deviceListView);
        oAdapter = new DeviceListAdapter(oData);
        m_oListView.setAdapter(oAdapter);
/*
        m_oListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                int check_position = m_oListView.getCheckedItemPosition();   //리스트뷰의 포지션을 가져옴.
                String vo = (String) adapterView.getAdapter().getItem(i);  //리스트뷰의 포지션 내용을 가져옴.
                Log.d("RRRRRRRRRRRRRRRRRRR", vo);
            }
        });

 */
    }


    @Override
    public void onClick(View v) {
        int nViewTag = Integer.parseInt((String)v.getTag());  //수정이냐 삭제냐
        String strViewName = "";

        View oParentView = (View)v.getParent(); // 부모의 View를 가져온다. 즉, 아이템 View임.

        TextView oTextNickname = (TextView) oParentView.findViewById(R.id.textView);
        nick = (String) oTextNickname.getText();
        position = (String) oParentView.getTag();

        switch (nViewTag)
        {
            case 1: // 수정
                strViewName = "수정";
                //oParentView = (View)oParentView .getParent();

                final EditText editText = new EditText(ManagementActivity.this);
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
                final AlertDialog.Builder editAD = new AlertDialog.Builder(ManagementActivity.this);
                editAD.setTitle("닉네임 변경")
                        .setMessage("변경할 닉네임을 입력하세요")
                        .setView(editText)
                        .setNegativeButton("취소", null)
                        .setPositiveButton("확인", null);
                final AlertDialog alert = editAD.create();
                alert.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button b = alert.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String newNickname = editText.getText().toString();
                                if (newNickname.equals("")) {
                                    Toast.makeText(getApplicationContext(), "닉네임을 입력하세요.", Toast.LENGTH_SHORT).show();
                                } else {
                                    db.collection("gabriel")
                                            .whereEqualTo("nickname", nick)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            db.collection("gabriel").document(document.getId()).update("nickname", newNickname);
                                                            Log.d(TAG, document.getId() + " => " + document.getData());
                                                        }

                                                    } else {
                                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                                    }
                                                }
                                            });

                                    alert.dismiss();
                                    DeviceListViewItem oItem = new DeviceListViewItem();
                                    oItem.nickname = newNickname;
                                    oItem.onClickListener = ManagementActivity.this;
                                    oData.set(Integer.parseInt(position), oItem);
                                    int totalElements = oData.size();// arrayList의 요소의 갯수를 구한다.

                                    for (int index = 0; index < totalElements; index++) {
                                        //System.out.println(oData.get(index));
                                        Log.i("edit check", String.valueOf(oData.get(index)));
                                    }
                                    oAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                });
                alert.show();

                String strMsg = "선택한 아이템의 position 은 "+position+" 입니다.\nTitle 텍스트 :" + oTextNickname.getText();
                Toast myToast = Toast.makeText(this.getApplicationContext(), strMsg, Toast.LENGTH_SHORT);
                myToast.show();
                break;
            case 2: // 삭제
                strViewName = "삭제";
                //oParentView = (View)oParentView .getParent();

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("안내").setIcon(android.R.drawable.ic_dialog_alert).setMessage("삭제하시겠습니까?")
                        .setPositiveButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })   // positive, negative 는 좌우 선택
                        .setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.collection("gabriel")
                                        .whereEqualTo("nickname", nick)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        db.collection("gabriel").document(document.getId()).delete();
                                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                                    }
                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                }
                                                oData.remove(Integer.parseInt(position));
                                                int totalElements = oData.size();// arrayList의 요소의 갯수를 구한다.
                                                for (int index = 0; index < totalElements; index++) {
                                                    //System.out.println(oData.get(index));
                                                    Log.i("delete check", String.valueOf(oData.get(index)));
                                                }
                                                oAdapter = new DeviceListAdapter(oData);
                                                m_oListView.setAdapter(oAdapter);

                                                //oAdapter.notifyDataSetChanged();
                                            }
                                        });
                            }
                        })
                        .show();

                //oAdapter = new DeviceListAdapter(oData);

                String strMsg2 = "선택한 아이템의 position 은 "+position+" 입니다.\nTitle 텍스트 :" + oTextNickname.getText();
                Toast myToast2 = Toast.makeText(this.getApplicationContext(), strMsg2, Toast.LENGTH_SHORT);
                myToast2.show();


                break;
        }


        //String strMsg = "선택한 아이템의 position 은 "+position+" 입니다.\nTitle 텍스트 :" + oTextNickname.getText();
        //Toast myToast = Toast.makeText(this.getApplicationContext(), strMsg, Toast.LENGTH_SHORT);
        //myToast.show();

    }
}

























/*

    public void deleteDevice(View view) {
        db.collection("gabriel")
                .whereEqualTo("nickname", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("gabriel").document(document.getId()).delete();
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });




        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        CollectionReference itemsRef = rootRef.collection("yourCollection");
        Query query = itemsRef.whereEqualTo("field1", "x").whereEqualTo("field2", "y");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        itemsRef.document(document.getId()).delete();
                    }
                } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        }
        });



    }

public void updateNickname(View view) {

        }
 */