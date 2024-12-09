package com.example.finalproject.utils;//package com.example.finalproject;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class MessageWeatherListener extends WearableListenerService {
    // Define constants for broadcast action and weather data key
    public static final String ACTION_WEATHER_UPDATE = "com.example.finalproject.ACTION_WEATHER_UPDATE";
    public static final String EXTRA_WEATHER_DATA = "weather_data";

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        // Check if the message path is related to weather info
        if (messageEvent.getPath().equals("/weather_info")) {
            String message = new String(messageEvent.getData());
            // Display a notification with the weather data
            NotificationHelper.displayWeatherNotification(this, message);

            // Send a local broadcast with the received weather data
            Intent intent = new Intent(ACTION_WEATHER_UPDATE);
            intent.putExtra(EXTRA_WEATHER_DATA, message);
            // Broadcast the intent using LocalBroadcastManager
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }
}
