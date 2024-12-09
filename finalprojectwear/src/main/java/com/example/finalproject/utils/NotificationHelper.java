package com.example.finalproject.utils;// NotificationHelper.java
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.finalproject.R;
import com.example.finalproject.activity.MainActivity;

public class NotificationHelper {

    private static final String CHANNEL_ID = "weather_channel";

    @SuppressLint("MissingPermission")
    public static void displayWeatherNotification(Context context, String message) {
        // Create an Intent to open MainActivity on notification tap
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("weather_data", message);  // Pass the weather data to the intent
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Weather Info Received")
                .setContentText("Tap to view details")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Create the notification channel if necessary
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Weather Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // Display the notification
        notificationManager.notify(1, builder.build());
    }
}
