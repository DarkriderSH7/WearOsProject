package com.example.finalproject;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class MessageWeatherListener extends WearableListenerService {
    public static final String ACTION_WEATHER_UPDATE = "com.example.finalproject.ACTION_WEATHER_UPDATE";
    public static final String EXTRA_WEATHER_DATA = "weather_data";

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        if (messageEvent.getPath().equals("/weather_info")) {
            String message = new String(messageEvent.getData());
            displayNotification(message);

            // Send a local broadcast with the received message
            android.content.Intent intent = new android.content.Intent(ACTION_WEATHER_UPDATE);
            intent.putExtra(EXTRA_WEATHER_DATA, message);
            androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    private void displayNotification(String message) {
        // Your existing notification code remains the same
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "weather_channel")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Weather Info Received")
                .setContentText("Tap to view details")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("weather_channel", "Weather Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(1, builder.build());
        } else {
            Toast.makeText(this, "Cannot show notification without POST_NOTIFICATIONS permission.", Toast.LENGTH_SHORT).show();
        }
    }
}
