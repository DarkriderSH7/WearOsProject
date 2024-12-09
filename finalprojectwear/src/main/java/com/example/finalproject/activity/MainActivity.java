package com.example.finalproject.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Build;

import androidx.annotation.NonNull;

import com.example.finalproject.utils.MessageWeatherListener;
import com.example.finalproject.adapter.WeatherInfoAdapter;
import com.example.finalproject.databinding.ActivityMainBinding;

import android.Manifest;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Arrays;

// MainActivity.java
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 1;
    ActivityMainBinding binding;
    private BroadcastReceiver weatherUpdateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize BroadcastReceiver for weather updates
        weatherUpdateReceiver = new WeatherUpdateReceiver();

        // Handle edge-to-edge display adjustments
        adjustUIForEdgeDisplay();

        // Check permissions for notifications
        checkNotificationPermission();

        // Initialize RecyclerView with weather data if available
        init();
    }

    // Method to adjust the UI layout for system bars
    private void adjustUIForEdgeDisplay() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Method to check and request notification permission
    private void checkNotificationPermission() {
        // Check if the device's Android version is at least Tiramisu
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            // Check if the app does not already have the POST_NOTIFICATIONS permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                // Request the POST_NOTIFICATIONS permission from the user
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_CODE_POST_NOTIFICATIONS);
            }
        }
    }


    private void init() {
        String weatherData = getIntent().getStringExtra("weather_data");
        if (weatherData != null) {
            updateUIWithWeatherData(weatherData);
        } else {
            Toast.makeText(this, "No weather data available.", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to update ui based on received data
    private void updateUIWithWeatherData(String weatherData) {
        String[] items = weatherData.trim().split("\n");
        Arrays.sort(items, String.CASE_INSENSITIVE_ORDER);
        WeatherInfoAdapter adapter = new WeatherInfoAdapter(items);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
        binding.imageView2.setVisibility(View.GONE);  // Hide the placeholder image
    }

    // Called when the activity is resumed
    @Override
    protected void onResume() {
        super.onResume();
        // It registers the receiver to listen for weather updates
        LocalBroadcastManager.getInstance(this).registerReceiver(weatherUpdateReceiver, new IntentFilter(MessageWeatherListener.ACTION_WEATHER_UPDATE));
    }

    // Called when the activity is paused
    @Override
    protected void onPause() {
        super.onPause();
        // It unregisters the receiver to stop listening for weather updates
        LocalBroadcastManager.getInstance(this).unregisterReceiver(weatherUpdateReceiver);
    }

    // Called when the user responds to a permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check if the request is for POST_NOTIFICATIONS permission
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            // If permission is denied, showing a toast message
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission denied to post notifications.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // BroadcastReceiver that listens for weather update broadcasts
    private class WeatherUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Check if the received broadcast is a weather update
            if (MessageWeatherListener.ACTION_WEATHER_UPDATE.equals(intent.getAction())) {
                // Getting the weather data from the intent
                String weatherData = intent.getStringExtra(MessageWeatherListener.EXTRA_WEATHER_DATA);
                if (weatherData != null) {
                    // Update the UI with the received weather data
                    updateUIWithWeatherData(weatherData);
                }
            }
        }
    }

}
