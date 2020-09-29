package com.example.monitoring;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class CheckAccessActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "CheckAccessActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_access);

        Log.d(TAG, "check?");
        Intent intent = getIntent();

        final String mac = intent.getExtras().getString("mac");
        final String nickname = intent.getExtras().getString("nickname");
        final int traffic = intent.getExtras().getInt("traffic");


        new AlertDialog.Builder(CheckAccessActivity.this)
                .setMessage(traffic + " 을(를) 잘못된 알림으로 저장하시겠습니까?")     // 제목 부분 (직접 작성)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {      // 버튼1 (직접 작성)
                    public void onClick(DialogInterface dialog, int which){

                        db.collection("gabriel")
                                .whereEqualTo("nickname", nickname)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                db.collection("gabriel").document(document.getId()).update("delete", traffic);
                                                Log.d(TAG, document.getId() + " => " + document.getData());
                                            }

                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                    }
                                });
                        Toast.makeText(getApplicationContext(), "저장 되었습니다.", Toast.LENGTH_SHORT).show(); // 실행할 코드
                        Intent intent = new Intent(CheckAccessActivity.this,MenuActivity.class);
                        startActivity(intent);


                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {     // 버튼2 (직접 작성)
                    public void onClick(DialogInterface dialog, int which){
                        Toast.makeText(getApplicationContext(), "취소 누름", Toast.LENGTH_SHORT).show(); // 실행할 코드
                        Intent intent = new Intent(CheckAccessActivity.this,MenuActivity.class);
                        startActivity(intent);
                    }
                })
                .show();
    }
}
