package com.example.sanatkumarsaha.viscom2016;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class Notification extends IntentService {

    NotificationCompat.Builder notification;
    private static final int ID = 12051996;
    String name;
    Intent i;

    public Notification() {
        super("Notification");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("Laura","laga die");
        Bundle data = intent.getExtras();
        name = data.getString("name");
        notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);
        notification.setSmallIcon(R.drawable.common_google_signin_btn_icon_light);
        notification.setTicker("New Message!");
        notification.setShowWhen(true);
        notification.setContentTitle(name);
        notification.setContentText(" Click Here to view your New Message ");
        notification.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        notification.setDefaults(android.app.Notification.DEFAULT_VIBRATE | android.app.Notification.DEFAULT_LIGHTS);
        i = new Intent(this,MainActivity.class);
        i.putExtra("name", name);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(ID,notification.build());
    }

}