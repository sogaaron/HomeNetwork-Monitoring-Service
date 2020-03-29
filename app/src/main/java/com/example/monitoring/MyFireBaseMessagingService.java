/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.monitoring;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

//import androidx.work.OneTimeWorkRequest;
//import androidx.work.WorkManager;

/**
 * NOTE: There can only be one service in each app that receives FCM messages. If multiple
 * are declared in the Manifest then the first one will be chosen.
 *
 * In order to make this Java sample functional, you must remove the following from the Kotlin messaging
 * service in the AndroidManifest.xml:
 *
 * <intent-filter>
 *   <action android:name="com.google.firebase.MESSAGING_EVENT" />
 * </intent-filter>
 */
public class MyFireBaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    boolean onOff = false;

    RemoteMessage remoteMessage;

    /**
     * Called when message is received.
     *
     * @param remoteMessage2 Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage2) {
        remoteMessage = remoteMessage2;
        //String now = dateFormat.format (System.currentTimeMillis());

        db.collection("notificationTime")//.orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //String start = document.getString("start");
                                Timestamp timestampStart = document.getTimestamp("start");
                                Timestamp timestampEnd = document.getTimestamp("end");
                                boolean flag = document.getBoolean("flag");
                                if (flag == true){ //alarm x
                                    //시간변환
                                    Date date1 = timestampStart.toDate();
                                    Date date2 = timestampEnd.toDate();

                                    Calendar cal1 = Calendar.getInstance();
                                    Calendar cal2 = Calendar.getInstance();
                                    Calendar now = Calendar.getInstance();


                                    cal1.setTime(date1);
                                    cal2.setTime(date2);
                                    cal1.add(Calendar.HOUR_OF_DAY,9);
                                    cal2.add(Calendar.HOUR_OF_DAY,9);

                                    now.add(Calendar.HOUR_OF_DAY,9);
                                    Log.d(TAG,"now : "+ now.getTime());


                                    cal1.set(Calendar.YEAR,now.get(Calendar.YEAR));
                                    cal1.set(Calendar.MONTH,now.get(Calendar.MONTH));
                                    cal1.set(Calendar.DATE,now.get(Calendar.DATE));

                                    cal2.set(Calendar.YEAR,now.get(Calendar.YEAR));
                                    cal2.set(Calendar.MONTH,now.get(Calendar.MONTH));
                                    cal2.set(Calendar.DATE,now.get(Calendar.DATE));
                                    if(cal1.get(Calendar.HOUR_OF_DAY)>cal2.get(Calendar.HOUR_OF_DAY))
                                        cal1.add(Calendar.DATE,-1);


                                    String start = dateFormat.format(cal1.getTime());
                                    String end = dateFormat.format(cal2.getTime());
                                    Log.d(TAG,"start : "+ start + "  end : " + end);

                                    if (cal1.before(cal2)){
                                        if (cal1.before(now) && cal2.after(now)){
                                            onOff=true; //alarm x
                                            Log.d(TAG,"start<end true alarm x");
                                        }
                                        else {
                                            onOff = false; //alarm o
                                            Log.d(TAG,"start<end false alarm o");
                                        }
                                    }else if(cal1.after(cal2)){
                                        if (cal1.after(now) && cal2.after(now)){
                                            onOff = true; //alarm x
                                            Log.d(TAG,"start<end true alarm x");
                                        }
                                        else {
                                            onOff = false; //alarm o
                                            Log.d(TAG,"start<end false alarm o");
                                        }
                                    }

                                }
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                        if(remoteMessage.getData() != null){
                            //Log.d("FCM Log", "알림 메시지: " + remoteMessage.getNotification().getBody());
                            long messageTime = remoteMessage.getSentTime();
                            Date messageTime2 = new Date(messageTime);
                            String messageNickname = remoteMessage.getData().get("nickname");
                            String messageDevice = remoteMessage.getData().get("device");
                            String messageBody = remoteMessage.getData().get("body");//remoteMessage.getNotification().getBody();
                            String messageTitle = remoteMessage.getData().get("title"); //remoteMessage.getNotification().getTitle();

                            Log.d(TAG, "알림 메시지"+ messageTitle + "  " + messageBody);

                            Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
                            String channelIdHIGH = "Channel ID";
                            String channelIdDEFAULT = "Channel ID2";

                            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), channelIdHIGH)
                                    .setSmallIcon(R.drawable.ic_stat_ic_notification)
                                    .setContentTitle(messageTitle)
                                    .setContentText(messageBody)
                                    .setAutoCancel(true)
                                    .setSound(defaultSoundUri)
                                    .setContentIntent(pendingIntent);
                            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                            NotificationCompat.Builder notificationBuilder2 = new NotificationCompat.Builder(getApplicationContext(), channelIdDEFAULT)
                                    .setSmallIcon(R.drawable.ic_stat_ic_notification)
                                    .setContentTitle(messageTitle)
                                    .setContentText(messageBody)
                                    .setAutoCancel(true)
                                    .setSound(defaultSoundUri)
                                    .setContentIntent(pendingIntent);
                            NotificationManager notificationManager2 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                // Create the NotificationChannel
                                //String channelName = "Channel Name";
                                NotificationChannel channel;
                                if (onOff==false){
                                    channel = new NotificationChannel(channelIdHIGH, "HIGH", NotificationManager.IMPORTANCE_HIGH);
                                    Log.d(TAG, "HIGH" + onOff);
                                    notificationManager.createNotificationChannel(channel);
                                }else {
                                    channel = new NotificationChannel(channelIdDEFAULT, "DEFAULT", NotificationManager.IMPORTANCE_LOW);
                                    Log.d(TAG, "DEFAULT"+onOff);
                                    notificationManager2.createNotificationChannel(channel);
                                }
                            }

                            if (onOff==false){
                                PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
                                PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "My:Tag");
                                wakeLock.acquire(5000);

                                notificationManager.notify(0, notificationBuilder.build());
                            }else{
                                notificationManager2.notify(0, notificationBuilder2.build());

                            }

                            onOff = false;

                            // Add a new document with a generated id.
                            Map<String, Object> data = new HashMap<>();
                            data.put("nickname", messageNickname);
                            data.put("device", messageDevice);
                            data.put("text", messageBody);
                            data.put("time", new Timestamp(messageTime2));

                            db.collection("log")
                                    .add(data)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
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


// notificationBuilder 두개 만든거 맞는가?

    }



    // [END receive_message]


    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    /*
    private void scheduleJob() {
        // [START dispatch_job]
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
                .build();
        WorkManager.getInstance().beginWith(work).enqueue();
        // [END dispatch_job]
    }

     */

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Log.i(TAG, "알림 메시지"+ messageBody);


        Intent intent = new Intent(this, MenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);


        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_stat_ic_notification)
                        .setContentTitle(getString(R.string.fcm_message))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }




}