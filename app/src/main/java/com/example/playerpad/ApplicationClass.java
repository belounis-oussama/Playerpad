package com.example.playerpad;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class ApplicationClass extends Application {

    public static final String CHANNEL_ID1="channel1";
    public static final String CHANNEL_ID2="channel2";
    public static final String ACTION_PREVIOUS="actionprevious";
    public static final String ACTION_NEXT="actionnext";
    public static final String ACTION_PLAY="actionplay";

    @Override
    public void onCreate() {
        super.onCreate();

        creatNotificationChannel();
    }

    private void creatNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel1=
                    new NotificationChannel(CHANNEL_ID1,"Channel1", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Channel 1 Desc..");

            NotificationChannel channel2=
                    new NotificationChannel(CHANNEL_ID2,"Channel2", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Channel 2 Desc..");

            NotificationManager notificationManager=getSystemService(NotificationManager.class);

            notificationManager.createNotificationChannel(channel1);
            notificationManager.createNotificationChannel(channel2);
        }
    }
}
