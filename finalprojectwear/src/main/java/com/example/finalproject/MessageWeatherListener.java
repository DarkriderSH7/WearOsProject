package com.example.finalproject;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
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
        // Generate a random requestCode using current time
        long randomNumber = System.currentTimeMillis();
        int requestCode = (int) randomNumber;

        // Create an Intent to open MainActivity on notification tap
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_WEATHER_DATA, message);  // Pass the weather data to the intent

        // Create a PendingIntent that will be triggered when the notification is clicked
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Create an action for the wearable device (optional - if you have WearOS features)
        Intent wearActionIntent = new Intent(this, MainActivity.class);
        wearActionIntent.setAction("WEAR_ACTION");
        PendingIntent wearActionPendingIntent = PendingIntent.getBroadcast(
                this, 0, wearActionIntent, PendingIntent.FLAG_IMMUTABLE);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "weather_channel")
                .setSmallIcon(R.drawable.ic_notification) // Set your icon here
                .setContentTitle("Weather Info Received")
                .setContentText("Tap to view details")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent) // Set the PendingIntent for the main click action
                .addAction(R.drawable.ic_notification, "Wearable Action", wearActionPendingIntent) // Wearable Action
                .setAutoCancel(true); // Automatically dismiss the notification when tapped

        // Create notification channel if necessary for Android O (API 26) and higher
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "weather_channel", "Weather Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // Check if the app has permission to post notifications before showing it
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(1, builder.build());  // Show notification
        } else {
            // Request permission to post notifications if not granted
            Toast.makeText(this, "Cannot show notification without POST_NOTIFICATIONS permission.", Toast.LENGTH_SHORT).show();
        }
    }
}
