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

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        if (messageEvent.getPath().equals("/weather_info")) {
            // Message received
            String message = new String(messageEvent.getData());
            // Display a notification stating that info has been received
            displayNotification(message);
        }
    }

    private void displayNotification(String message) {
        // Build and display a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "weather_channel")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Weather Info Received")
                .setContentText("Tap to view details")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Create notification channel if necessary (for API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("weather_channel", "Weather Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // Check for POST_NOTIFICATIONS permission before notifying
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_GRANTED) {
            // Show the notification
            notificationManager.notify(1, builder.build());
        } else {
            // Permission not granted, cannot show notification
            Toast.makeText(this, "Cannot show notification without POST_NOTIFICATIONS permission.", Toast.LENGTH_SHORT).show();
        }
    }
}
