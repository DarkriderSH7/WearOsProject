package com.example.finalproject;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.finalproject.databinding.ActivityMainBinding;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    // Request code for notification permission
    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 1;
    ActivityMainBinding binding; // ViewBinding for accessing UI elements
    private BroadcastReceiver weatherUpdateReceiver; // BroadcastReceiver for handling weather updates

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout and initialize ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Handle edge-to-edge display adjustments
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Check and request POST_NOTIFICATIONS permission if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_CODE_POST_NOTIFICATIONS);
            }
        }

        // Initialize the BroadcastReceiver for weather updates
        weatherUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (MessageWeatherListener.ACTION_WEATHER_UPDATE.equals(intent.getAction())) {
                    // Get the weather data from the intent
                    String weatherData = intent.getStringExtra(MessageWeatherListener.EXTRA_WEATHER_DATA);
                    // Update the UI with the weather data
                    setAdapter(weatherData);
                }
            }
        };

        // Initialize data on activity creation
        init();
    }

    // Initialize activity with intent data if available
    private void init() {
        Intent notificationIntent = getIntent();
        // Check if data was passed through the intent
        String weatherData = notificationIntent.getStringExtra(MessageWeatherListener.EXTRA_WEATHER_DATA);

        // Only update the adapter if data is not null
        if (weatherData != null) {
            setAdapter(weatherData);  // Update the RecyclerView with the weather data
        } else {
            // Handle the case where no data is passed (optional)
            Toast.makeText(this, "No weather data available.", Toast.LENGTH_SHORT).show();
        }
    }


    // Configure the RecyclerView with weather data
    private void setAdapter(String weatherData) {
        // Split the weather data string by '$'
        String[] items = weatherData.trim().split("\\$");

        // Sort the items alphabetically (case-insensitive)
        Arrays.sort(items, String.CASE_INSENSITIVE_ORDER);

        // Set up the RecyclerView with a LinearLayoutManager
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create and set the RecyclerView adapter with the sorted data
        com.example.finalproject.MyAdapter adapter = new com.example.finalproject.MyAdapter(items);

        // Hide the placeholder image once data is displayed
        binding.imageView2.setVisibility(View.GONE);

        // Attach the adapter to the RecyclerView
        binding.recyclerView.setAdapter(adapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Register the weather update BroadcastReceiver
        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this)
                .registerReceiver(weatherUpdateReceiver, new IntentFilter(MessageWeatherListener.ACTION_WEATHER_UPDATE));
    }

    @Override
    protected void onPause() {
        // Unregister the weather update BroadcastReceiver
        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(weatherUpdateReceiver);
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, no action needed
            } else {
                // Show a message if permission is denied
                Toast.makeText(this, "Permission denied to post notifications.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
