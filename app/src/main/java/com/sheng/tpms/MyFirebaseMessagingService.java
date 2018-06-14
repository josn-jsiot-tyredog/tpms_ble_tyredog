package com.sheng.tpms;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by sheng on 2017/12/4.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String message = remoteMessage.getNotification().getBody();
        Log.d("fcm message", remoteMessage.getNotification().getBody());
        String Data = remoteMessage.getData().get("title");
        String Data1 = remoteMessage.getData().get("body");
        if (message != null) {
            Log.d("fcm message", "Message: "+message);
//            sendNotification("SYSTEM", id+Data+Data1+message);
//            sendNotification("SYSTEM", message);
        }
    }

    private void sendNotification(String title, String messageBody) {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, blescreenMain.class), 0);
        Notification.Builder notificationBuilder = new Notification.Builder(this)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_SOUND)
                .setSmallIcon(R.mipmap.ic_tyredog)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setContentIntent(contentIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(10, notificationBuilder.build());
        }
    }
}
