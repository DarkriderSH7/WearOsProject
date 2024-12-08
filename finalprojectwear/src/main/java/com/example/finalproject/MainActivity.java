package com.example.finalproject;

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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 1;
    ActivityMainBinding binding;
    private BroadcastReceiver weatherUpdateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Handle edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
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

        // Initialize the BroadcastReceiver
        weatherUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (MessageWeatherListener.ACTION_WEATHER_UPDATE.equals(intent.getAction())) {
                    // Get the weather data from the intent
                    String weatherData = intent.getStringExtra(MessageWeatherListener.EXTRA_WEATHER_DATA);
                    // Update the UI
                    binding.welcomeText.setText(weatherData);
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the receiver
        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this)
                .registerReceiver(weatherUpdateReceiver, new IntentFilter(MessageWeatherListener.ACTION_WEATHER_UPDATE));
    }

    @Override
    protected void onPause() {
        // Unregister the receiver
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
                // Permission granted
            } else {
                Toast.makeText(this, "Permission denied to post notifications.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
