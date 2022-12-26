package com.example.chatapp.Classes;

import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

public class SoundManager {
    private static final Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    public static void makeSound(Context context){

        MediaPlayer mediaPlayer = MediaPlayer.create(context, defaultSoundUri);
        mediaPlayer.start();
    }
    public static void makeNotification(Context context){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle("Notification")
                .setContentText("This is a notification.")
                .setAutoCancel(true)
                .setSound(defaultSoundUri);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }
}
