package com.example.monitoring;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MenuActivity extends AppCompatActivity {

    public static final int Request_Code = 1001;
    public static final int Request_Code2 = 1002;
    public static final int Request_Code3 = 1003;

    Button b1,b2, b3, b4, b5;
    View view;
    String token;
    FirebaseFirestore db = FirebaseFirestore.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("firebase", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token

                        token = task.getResult().getToken();

                        db.collection("token").document("toktok")
                                .update(
                                        "tok", token
                                );

                        // Log and toast
                        //String msg = getString(R.string.msg_token_fmt, token);
                        Log.d("firebase", token);
                        Toast.makeText(MenuActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });


        view = findViewById(R.id.menu_layout);
        b1 = findViewById(R.id.button1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this,TestGraphActivity.class);
                //  intent.putExtra("msg","아론");
                intent.putExtra("title",b1.getText().toString());
                startActivity(intent);
                //startActivityForResult(intent,Request_Code);
            }
        });

        b2 = findViewById(R.id.button2);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this,RegisterActivity.class);
                //  intent.putExtra("msg","아론");
                intent.putExtra("title",b2.getText().toString());
                startActivity(intent);
                //startActivityForResult(intent,Request_Code2);
            }
        });
        b3 = findViewById(R.id.button3);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this,ManagementActivity.class);
                //  intent.putExtra("msg","아론");
                intent.putExtra("title",b2.getText().toString());
                startActivity(intent);
                //startActivityForResult(intent,Request_Code2);
            }
        });
        b4 = findViewById(R.id.button4);
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this,LogActivity.class);
                intent.putExtra("title",b2.getText().toString());
                startActivity(intent);
                //startActivityForResult(intent,Request_Code2);
            }
        });
        b5 = findViewById(R.id.button5);
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this,SettingActivity.class);
                intent.putExtra("title",b2.getText().toString());
                startActivity(intent);
                //startActivityForResult(intent,Request_Code2);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Request_Code){
            if(resultCode == RESULT_OK){
                //  Snackbar.make(view, data.getStringExtra("번호"),Snackbar.LENGTH_LONG).show();
                final Snackbar sn = Snackbar.make(view,"memo1 save",Snackbar.LENGTH_INDEFINITE);
                sn.setAction("확인", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sn.dismiss();
                    }
                });
                sn.show();
                b1.setBackgroundColor(Color.RED);
            }
        }
        if(requestCode == Request_Code2){
            if(resultCode == RESULT_OK){
                //  Snackbar.make(view, data.getStringExtra("번호"),Snackbar.LENGTH_LONG).show();
                final Snackbar sn = Snackbar.make(view,"memo2 save",Snackbar.LENGTH_INDEFINITE);
                sn.setAction("확인", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sn.dismiss();
                    }
                });
                sn.show();
                b1.setBackgroundColor(Color.BLUE);
            }
        }
    }
}
