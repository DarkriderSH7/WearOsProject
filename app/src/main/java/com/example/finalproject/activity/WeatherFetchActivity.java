package com.example.finalproject.activity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.example.finalproject.R;
import com.example.finalproject.databinding.ActivityMainBinding;
import com.example.finalproject.models.WeatherData;
import com.example.finalproject.repositories.WeatherRepository;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WeatherFetchActivity extends AppCompatActivity {

    // Activity binding variable
    private ActivityMainBinding binding;
    private WeatherRepository weatherRepository;
    private final Set<CardView> selectedCards = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        weatherRepository = new WeatherRepository();

        // Set OnClickListeners for card selection
        binding.cardDescription.setOnClickListener(v -> handleCardSelection(binding.cardDescription));
        binding.cardTemperature.setOnClickListener(v -> handleCardSelection(binding.cardTemperature));
        binding.cardPressure.setOnClickListener(v -> handleCardSelection(binding.cardPressure));
        binding.cardHumidity.setOnClickListener(v -> handleCardSelection(binding.cardHumidity));
        binding.cardWindSpeed.setOnClickListener(v -> handleCardSelection(binding.cardWindSpeed));
        binding.cardPrecipitation.setOnClickListener(v -> handleCardSelection(binding.cardPrecipitation));

        // Set OnClickListener for "Send to Watch" Button
        binding.btnSendToWatch.setOnClickListener(v -> {
            if (selectedCards.isEmpty()) {
                Toast.makeText(WeatherFetchActivity.this, "No attributes selected to send.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Build the data to send
            StringBuilder selectedAttributes = new StringBuilder();

            for (CardView card : selectedCards) {
                TextView title = card.findViewById(
                        card == binding.cardDescription ? R.id.tv_weather_description_title :
                                card == binding.cardTemperature ? R.id.tv_weather_temperature_title :
                                        card == binding.cardPressure ? R.id.tv_weather_pressure_title :
                                                card == binding.cardHumidity ? R.id.tv_weather_humidity_title :
                                                        card == binding.cardWindSpeed ? R.id.tv_weather_wind_speed_title :
                                                                R.id.tv_weather_precipitation_title
                );
                TextView value = card.findViewById(
                        card == binding.cardDescription ? R.id.tv_weather_description :
                                card == binding.cardTemperature ? R.id.tv_weather_temperature :
                                        card == binding.cardPressure ? R.id.tv_weather_pressure :
                                                card == binding.cardHumidity ? R.id.tv_weather_humidity :
                                                        card == binding.cardWindSpeed ? R.id.tv_weather_wind_speed :
                                                                R.id.tv_weather_precipitation
                );

                selectedAttributes.append(title.getText().toString())
                        .append(": ")
                        .append(value.getText().toString())
                        .append("\n");
            }

            String message = selectedAttributes.toString();

            // Send the message to the connected nodes
            Task<List<Node>> nodeListTask = Wearable.getNodeClient(WeatherFetchActivity.this).getConnectedNodes();
            nodeListTask.addOnSuccessListener(nodes -> {
                if (nodes.isEmpty()) {
                    Toast.makeText(WeatherFetchActivity.this, "No connected wearable device found.", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (Node node : nodes) {
                    Task<Integer> sendMessageTask =
                            Wearable.getMessageClient(WeatherFetchActivity.this).sendMessage(
                                    node.getId(), "/weather_info", message.getBytes());

                    sendMessageTask.addOnSuccessListener(integer -> {
                        Toast.makeText(WeatherFetchActivity.this, "Message sent to " + node.getDisplayName(), Toast.LENGTH_SHORT).show();
                    });

                    sendMessageTask.addOnFailureListener(e -> {
                        Toast.makeText(WeatherFetchActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });

        // Set OnClickListener for "Fetch Weather" Button
        binding.btnFetchWeather.setOnClickListener(v -> {
            String cityName = binding.etCityName.getText().toString().trim();
            if (!cityName.isEmpty()) {
                fetchWeatherData(cityName);
            } else {
                Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Handles the selection of a CardView.
     * Toggles the background color of the clicked card and updates the selectedCards set.
     *
     * @param card The CardView that was clicked.
     */
    private void handleCardSelection(CardView card) {
        if (selectedCards.contains(card)) {
            // Deselect the card
            card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white));
            updateCardTextColor(card, false);
            selectedCards.remove(card);
        } else {
            // Select the card
            card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.light_blue));
            updateCardTextColor(card, true);
            selectedCards.add(card);
        }
    }

    /**
     * Updates the text color of the CardView's TextViews.
     *
     * @param card       The CardView whose text colors are to be updated.
     * @param isSelected Whether the card is selected.
     */
    private void updateCardTextColor(CardView card, boolean isSelected) {
        if (card == binding.cardDescription) {

            binding.tvWeatherDescriptionTitle.setTextColor(isSelected ? ContextCompat.getColor(this, R.color.white) : ContextCompat.getColor(this, R.color.purple_500));
            binding.tvWeatherDescription.setTextColor(isSelected ? ContextCompat.getColor(this, R.color.white) : ContextCompat.getColor(this, R.color.black));
        } else if (card == binding.cardTemperature) {

            binding.tvWeatherTemperatureTitle.setTextColor(isSelected ? ContextCompat.getColor(this, R.color.white) : ContextCompat.getColor(this, R.color.purple_500));
            binding.tvWeatherTemperature.setTextColor(isSelected ? ContextCompat.getColor(this, R.color.white) : ContextCompat.getColor(this, R.color.black));
        } else if (card == binding.cardPressure) {

            binding.tvWeatherPressureTitle.setTextColor(isSelected ? ContextCompat.getColor(this, R.color.white) : ContextCompat.getColor(this, R.color.purple_500));
            binding.tvWeatherPressure.setTextColor(isSelected ? ContextCompat.getColor(this, R.color.white) : ContextCompat.getColor(this, R.color.black));
        } else if (card == binding.cardHumidity) {

            binding.tvWeatherHumidityTitle.setTextColor(isSelected ? ContextCompat.getColor(this, R.color.white) : ContextCompat.getColor(this, R.color.purple_500));
            binding.tvWeatherHumidity.setTextColor(isSelected ? ContextCompat.getColor(this, R.color.white) : ContextCompat.getColor(this, R.color.black));
        } else if (card == binding.cardWindSpeed) {

            binding.tvWeatherWindSpeedTitle.setTextColor(isSelected ? ContextCompat.getColor(this, R.color.white) : ContextCompat.getColor(this, R.color.purple_500));
            binding.tvWeatherWindSpeed.setTextColor(isSelected ? ContextCompat.getColor(this, R.color.white) : ContextCompat.getColor(this, R.color.black));
        } else if (card == binding.cardPrecipitation) {

            binding.tvWeatherPrecipitationTitle.setTextColor(isSelected ? ContextCompat.getColor(this, R.color.white) : ContextCompat.getColor(this, R.color.purple_500));
            binding.tvWeatherPrecipitation.setTextColor(isSelected ? ContextCompat.getColor(this, R.color.white) : ContextCompat.getColor(this, R.color.black));
        }
    }


    /**
     * Fetches weather data using the WeatherRepository and updates the UI.
     *
     * @param cityName The city name to fetch weather for.
     */
    private void fetchWeatherData(String cityName) {
        // Call to fetch data
        weatherRepository.fetchWeatherData("London", new WeatherRepository.WeatherCallback() {
            @Override
            public void onSuccess(WeatherData weatherData) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Updating UI with weatherData
                        updateUI(weatherData);
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                // Handle the error on UI thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Show error message to the user
                        showError(errorMessage);
                    }
                });
            }
        });
    }

    /**
     * Parses the weather data and updates the UI.
     *
     * @param weatherData The weather data object.
     */

    // update the UI with fetched data
    private void updateUI(WeatherData weatherData) {
        binding.tvCityName.setText("City: " + weatherData.getCityName());
        binding.tvWeatherDescription.setText(weatherData.getWeatherDescription());
        binding.tvWeatherTemperature.setText(String.format("%.1f°C", weatherData.getMain().getTemperature()));
        binding.tvWeatherPressure.setText(weatherData.getMain().getPressure() + " hPa");
        binding.tvWeatherHumidity.setText(weatherData.getMain().getHumidity() + "%");
        binding.tvWeatherWindSpeed.setText(weatherData.getWind().getSpeed() + " m/s");

        String precipitation = "N/A";
        if (weatherData.getRain() != null) {
            precipitation = weatherData.getRain().getOneHour() + " mm";
        } else if (weatherData.getSnow() != null) {
            precipitation = weatherData.getSnow().getOneHour() + " mm";
        }
        binding.tvWeatherPrecipitation.setText(precipitation);
    }

    // Handling Error
    private void showError(String errorMessage) {
        Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
    }
}
