package com.example.monitoring;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    public InputFilter filterAlphaNum = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
            if(!ps.matcher(source).matches()){
                return "";
            }
            return null;
        }
    };

    private class ExampleThread extends Thread {
        private static final String TAG = "ExampleThread";
        public ExampleThread() { // 초기화 작업

        }
        public void run () { // 스레드에게 수행시킬 동작들 구현
            execute();
        }
    }

    EditText et,et2;
    Button b1,b2, b3;
    View view;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Map<String, Object> deviceInfo = new HashMap<>();
    ArrayList<String> arr = new ArrayList<>();
    private  final String TAG = "RegisterActivity";//getClass().getSimpleName().trim();
    static long prev = 0, now,gap;
    ArrayList<String> unregisetered_List = new ArrayList();
    ArrayList<String> unregisetered_VendorList = new ArrayList();

    String[] items;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        view = findViewById(R.id.reg_layout);
        et = findViewById(R.id.macText);
        et.setFilters(new InputFilter[]{filterAlphaNum});
        et2 = findViewById(R.id.nicknameText);
        b1 = findViewById(R.id.ok_button);
        b2 = findViewById(R.id.cancel_button);
        b3 = findViewById(R.id.search_button);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String macinput = et.getText().toString();
                String nickinput = et2.getText().toString();

                macinput = macinput.trim().replace(" ","");
                nickinput = nickinput.trim().replace(" ","");
                if (macinput.equals("") || nickinput.equals("")){
                    Toast.makeText(getApplicationContext(),"값을 입력하세요.",Toast.LENGTH_SHORT).show();
                }
                else{
                    deviceInfo.put("mac", macinput);
                    deviceInfo.put("nickname", nickinput);
                    deviceInfo.put("normal", 0);
                    deviceInfo.put("type",0);

                    InputMethodManager mInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    mInputMethodManager.hideSoftInputFromWindow(et2.getWindowToken(), 0);
                    //자판 내리기

                    et.setText(null);
                    et2.setText(null);
                    //내용 지우기


                    final String finalMacinput = macinput;
                    db.collection("gabriel").document(macinput)
                            .set(deviceInfo)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void v) {
                                    Log.d(TAG, "DocumentSnapshot added with ID: ");
                                    /*
                                    final Snackbar sn = Snackbar.make(view, "Register Success", Snackbar.LENGTH_INDEFINITE);
                                    sn.setAction("확인", new -;View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            sn.dismiss();
                                        }
                                    });
                                    sn.show();
                                    */

                                    AlertDialog.Builder editAD = new AlertDialog.Builder(RegisterActivity.this);
                                    editAD.setTitle("")
                                            .setMessage("등록이 완료되었습니다. 이벤트를 등록하시겠습니까?")
                                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent(RegisterActivity.this,Training2Activity.class);
                                                    intent.putExtra("mac", finalMacinput);
                                                    startActivity(intent);
                                                }
                                            })
                                            .setNegativeButton("취소", null);
                                    AlertDialog alert = editAD.create();
                                    alert.show();
                                    Log.d("gggggggggg","ff");


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                }
                            });
                }
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {      // 미등록 리스트 보여주기
            @Override
            public void onClick(View v) {
                // Instantiate the RequestQueue.
                ExampleThread thr = new ExampleThread();
                thr.start();    // execute 함수 멀티쓰레드로 실행
//                new Thread(new Runnable() {
//                    @Override public void run() { // TODO Auto-generated method stub
//                        execute();
//                    }
//                }).start();

            }
        });
    }

    int i=0;
    public void execute(){
        RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
        String url ="https://us-central1-packet-monitoring-system.cloudfunctions.net/From-Android-To-Device?message=search";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

        Log.w(TAG,"wowowowowowowowowowowo");
        now = System.currentTimeMillis();
        gap = now - prev;

        if(gap > 10000)
            try {
                Thread.sleep(7000);   //  파이어스토어에 저장되는데 걸리는 시간 고려
            }catch (Exception e){

            }

        ////////// 미등록 리스트 받기
        db.collection("list").orderBy("time", Query.Direction.DESCENDING).limit(1)      // 가장 최신 리스트 하나
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(gap > 10000 ){       // 10초 지난 이후에 다시 누를때는 새로 불러옴, 10초 이내는 그냥 바로 전 데이터 불러옴
                                unregisetered_VendorList.clear();

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    unregisetered_List = (ArrayList)document.get("list");
                                }

                                new Thread(new Runnable() {     // renderData 함수 멀티 쓰레드로 실행
                                    @Override public void run() { // TODO Auto-generated method stub
                                        for (final String arguments : unregisetered_List) {
                                            Log.e("mac: ",arguments);
                                            String v = arguments+"("+get(arguments)+")";
//                                            Log.e("vendor : ",v);
                                            unregisetered_VendorList.add(v);
                                            try {
                                                Thread.sleep(1300);
                                            }catch (Exception e){

                                            }
                                        }
                                    }
                                }).start();

                                try {
                                    Thread.sleep(1300*unregisetered_List.size()+500);
                                }catch (Exception e){

                                }
                            }

                            prev = now;

                            items = new String[unregisetered_VendorList.size()];
                            items = unregisetered_VendorList.toArray(items);
                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);

                            builder.setTitle("미등록 기기 리스트");

                            builder.setItems(items, new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int pos)
                                {
                                    Toast.makeText(getApplicationContext(),items[pos],Toast.LENGTH_LONG).show();
                                    YesNo(items[pos]);

                                }
                            });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private static final String baseURL = "http://api.macvendors.com/";

    /** Performs lookup on supplied MAC address.
     * @param macAddress MAC address to lookup.
     * @return Manufacturer of MAC address. */
    private static String get(String macAddress) {
        try {
            StringBuilder result = new StringBuilder();
            URL url = new URL(baseURL + macAddress);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            return result.toString();
        } catch (FileNotFoundException e) {
            // MAC not found
            return "N/A";
        } catch (IOException e) {
            // Error during lookup, either network or API.
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void YesNo(final String mac){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("안내").setIcon(android.R.drawable.ic_dialog_alert).setMessage("등록하시겠습니까?")
                .setPositiveButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })   // positive, negative 는 좌우 선택
                .setNegativeButton("등록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        et.setText(mac);
                    }
                })
                .show();
    }


    public void backToMenu(View view){

        Intent intent = new Intent(this,MenuActivity.class);

        setResult(RESULT_OK,intent);
        finish();
    }


}
