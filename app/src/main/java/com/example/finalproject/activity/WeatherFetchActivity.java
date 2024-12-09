//package com.example.finalproject.activity;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.cardview.widget.CardView;
//import androidx.core.content.ContextCompat;
//
//import com.example.finalproject.R;
//import com.example.finalproject.databinding.ActivityMainBinding;
//import com.google.android.gms.tasks.Task;
//import com.google.android.gms.wearable.Node;
//import com.google.android.gms.wearable.Wearable;
//
//import android.os.Bundle;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//
//import java.io.IOException;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//public class WeatherFetchActivity extends AppCompatActivity {
//    ActivityMainBinding binding;
//
//    // Set to hold all selected CardViews
//    private Set<CardView> selectedCards = new HashSet<>();
//
//    private final String API_KEY = "8048ad7b856423eff0e08e7df8062a9d";
//    private final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather?q=";
//
//    // Declare card references
//    private CardView cardDescription;
//    private CardView cardTemperature;
//    private CardView cardPressure;
//    private CardView cardHumidity;
//    private CardView cardWindSpeed;
//    private CardView cardPrecipitation;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        // Initialize CardViews
//        cardDescription = findViewById(R.id.card_description);
//        cardTemperature = findViewById(R.id.card_temperature);
//        cardPressure = findViewById(R.id.card_pressure);
//        cardHumidity = findViewById(R.id.card_humidity);
//        cardWindSpeed = findViewById(R.id.card_wind_speed);
//        cardPrecipitation = findViewById(R.id.card_precipitation);
//
//        // Set OnClickListeners for card selection
//        cardDescription.setOnClickListener(v -> handleCardSelection(cardDescription));
//        cardTemperature.setOnClickListener(v -> handleCardSelection(cardTemperature));
//        cardPressure.setOnClickListener(v -> handleCardSelection(cardPressure));
//        cardHumidity.setOnClickListener(v -> handleCardSelection(cardHumidity));
//        cardWindSpeed.setOnClickListener(v -> handleCardSelection(cardWindSpeed));
//        cardPrecipitation.setOnClickListener(v -> handleCardSelection(cardPrecipitation));
//
//        // Set OnClickListener for "Send to Watch" Button
//        binding.btnSendToWatch.setOnClickListener(v -> {
//            if (selectedCards.isEmpty()) {
//                Toast.makeText(WeatherFetchActivity.this, "No attributes selected to send.", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            // Build the data to send
//            StringBuilder selectedAttributes = new StringBuilder();
//            for (CardView card : selectedCards) {
//                TextView title = card.findViewById(
//                        card == cardDescription ? R.id.tv_weather_description_title :
//                                card == cardTemperature ? R.id.tv_weather_temperature_title :
//                                        card == cardPressure ? R.id.tv_weather_pressure_title :
//                                                card == cardHumidity ? R.id.tv_weather_humidity_title :
//                                                        card == cardWindSpeed ? R.id.tv_weather_wind_speed_title :
//                                                                R.id.tv_weather_precipitation_title
//                );
//                TextView value = card.findViewById(
//                        card == cardDescription ? R.id.tv_weather_description :
//                                card == cardTemperature ? R.id.tv_weather_temperature :
//                                        card == cardPressure ? R.id.tv_weather_pressure :
//                                                card == cardHumidity ? R.id.tv_weather_humidity :
//                                                        card == cardWindSpeed ? R.id.tv_weather_wind_speed :
//                                                                R.id.tv_weather_precipitation
//                );
//
//                selectedAttributes.append(title.getText().toString())
//                        .append(": ")
//                        .append(value.getText().toString())
//                        .append("\n$");
//            }
//
//            String message = selectedAttributes.toString();
//
//            // Send the message to the connected nodes
//            Task<List<Node>> nodeListTask = Wearable.getNodeClient(WeatherFetchActivity.this).getConnectedNodes();
//            nodeListTask.addOnSuccessListener(nodes -> {
//                if (nodes.isEmpty()) {
//                    Toast.makeText(WeatherFetchActivity.this, "No connected wearable device found.", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                for (Node node : nodes) {
//                    Task<Integer> sendMessageTask =
//                            Wearable.getMessageClient(WeatherFetchActivity.this).sendMessage(
//                                    node.getId(), "/weather_info", message.getBytes());
//
//                    sendMessageTask.addOnSuccessListener(integer -> {
//                        Toast.makeText(WeatherFetchActivity.this, "Message sent to " + node.getDisplayName(), Toast.LENGTH_SHORT).show();
//                    });
//
//                    sendMessageTask.addOnFailureListener(e -> {
//                        Toast.makeText(WeatherFetchActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
//                    });
//                }
//            });
//        });
//
//        binding.btnFetchWeather.setOnClickListener(v -> {
//            String cityName = binding.etCityName.getText().toString().trim();
//            if (!cityName.isEmpty()) {
//                fetchWeatherData(cityName);
//            } else {
//                Toast.makeText(WeatherFetchActivity.this, "Please enter a city name", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    /**
//     * Handles the selection of a CardView.
//     * Toggles the background color of the clicked card and updates the selectedCards set.
//     *
//     * @param card The CardView that was clicked.
//     */
//    private void handleCardSelection(CardView card) {
//        if (selectedCards.contains(card)) {
//            // Deselect the card
//            card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white));
//            updateCardTextColor(card, false);
//            selectedCards.remove(card);
//        } else {
//            // Select the card
//            card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.light_blue));
//            updateCardTextColor(card, true);
//            selectedCards.add(card);
//        }
//    }
//
//    /**
//     * Updates the text color of the CardView's TextViews.
//     *
//     * @param card The CardView whose text colors are to be updated.
//     * @param isSelected Whether the card is selected.
//     */
//    private void updateCardTextColor(CardView card, boolean isSelected) {
//        if (card == cardDescription) {
//            TextView title = card.findViewById(R.id.tv_weather_description_title);
//            TextView value = card.findViewById(R.id.tv_weather_description);
//            title.setTextColor(isSelected ? ContextCompat.getColor(this, R.color.white) : ContextCompat.getColor(this, R.color.purple_500));
//            value.setTextColor(isSelected ? ContextCompat.getColor(this, R.color.white) : ContextCompat.getColor(this, R.color.black));
//        } else if (card == cardTemperature) {
//            TextView title = card.findViewById(R.id.tv_weather_temperature_title);
//            TextView value = card.findViewById(R.id.tv_weather_temperature);
//            title.setTextColor(isSelected ? ContextCompat.getColor(this, R.color.white) : ContextCompat.getColor(this, R.color.purple_500));
//            value.setTextColor(isSelected ? ContextCompat.getColor(this, R.color.white) : ContextCompat.getColor(this, R.color.black));
//        } else if (card == cardPressure) {
//            TextView title = card.findViewById(R.id.tv_weather_pressure_title);
//            TextView value = card.findViewById(R.id.tv_weather_pressure);
//            title.setTextColor(isSelected ? ContextCompat.getColor(this, R.color.white) : ContextCompat.getColor(this, R.color.purple_500));
//            value.setTextColor(isSelected ? ContextCompat.getColor(this, R.color.white) : ContextCompat.getColor(this, R.color.black));
//        } else if (card == cardHumidity) {
//            TextView title = card.findViewById(R.id.tv_weather_humidity_title);
//            TextView value = card.findViewById(R.id.tv_weather_humidity);
//            title.setTextColor(isSelected ? ContextCompat.getColor(this, R.color.white) : ContextCompat.getColor(this, R.color.purple_500));
//            value.setTextColor(isSelected ? ContextCompat.getColor(this, R.color.white) : ContextCompat.getColor(this, R.color.black));
//        } else if (card == cardWindSpeed) {
//            TextView title = card.findViewById(R.id.tv_weather_wind_speed_title);
//            TextView value = card.findViewById(R.id.tv_weather_wind_speed);
//            title.setTextColor(isSelected ? ContextCompat.getColor(this, R.color.white) : ContextCompat.getColor(this, R.color.purple_500));
//            value.setTextColor(isSelected ? ContextCompat.getColor(this, R.color.white) : ContextCompat.getColor(this, R.color.black));
//        } else if (card == cardPrecipitation) {
//            TextView title = card.findViewById(R.id.tv_weather_precipitation_title);
//            TextView value = card.findViewById(R.id.tv_weather_precipitation);
//            title.setTextColor(isSelected ? ContextCompat.getColor(this, R.color.white) : ContextCompat.getColor(this, R.color.purple_500));
//            value.setTextColor(isSelected ? ContextCompat.getColor(this, R.color.white) : ContextCompat.getColor(this, R.color.black));
//        }
//    }
//
//    private void fetchWeatherData(String cityName) {
//        String url = BASE_URL + cityName + "&appid=" + API_KEY + "&units=metric";
//
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url(url)
//                .build();
//
//        // Show a loading indicator
//        binding.btnFetchWeather.setEnabled(false);
//        binding.btnFetchWeather.setText("Fetching...");
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//                runOnUiThread(() -> {
//                    binding.btnFetchWeather.setEnabled(true);
//                    binding.btnFetchWeather.setText("Fetch Weather");
//                    Toast.makeText(WeatherFetchActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
//                });
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                runOnUiThread(() -> {
//                    binding.btnFetchWeather.setEnabled(true);
//                    binding.btnFetchWeather.setText("Fetch Weather");
//                });
//
//                if (response.isSuccessful()) {
//                    final String responseData = response.body().string();
//                    runOnUiThread(() -> parseAndDisplayWeather(responseData));
//                } else {
//                    runOnUiThread(() -> Toast.makeText(WeatherFetchActivity.this, "City not found", Toast.LENGTH_SHORT).show());
//                }
//            }
//        });
//    }
//
//    private void parseAndDisplayWeather(String responseData) {
//        try {
//            JsonObject jsonObject = JsonParser.parseString(responseData).getAsJsonObject();
//            String cityName = jsonObject.get("name").getAsString();
//            String weatherDescription = jsonObject
//                    .getAsJsonArray("weather")
//                    .get(0)
//                    .getAsJsonObject()
//                    .get("description")
//                    .getAsString();
//            double temperature = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
//            int pressure = jsonObject.getAsJsonObject("main").get("pressure").getAsInt();
//            int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
//            double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
//
//            // Precipitation might be under "rain" or "snow"
//            String precipitation = "N/A";
//            if (jsonObject.has("rain")) {
//                JsonObject rain = jsonObject.getAsJsonObject("rain");
//                if (rain.has("1h")) {
//                    precipitation = rain.get("1h").getAsDouble() + " mm";
//                } else if (rain.has("3h")) {
//                    precipitation = rain.get("3h").getAsDouble() + " mm";
//                }
//            } else if (jsonObject.has("snow")) {
//                JsonObject snow = jsonObject.getAsJsonObject("snow");
//                if (snow.has("1h")) {
//                    precipitation = snow.get("1h").getAsDouble() + " mm";
//                } else if (snow.has("3h")) {
//                    precipitation = snow.get("3h").getAsDouble() + " mm";
//                }
//            }
//
//            // Update UI with fetched data
//            binding.tvCityName.setText("City: " + cityName);
//            binding.tvWeatherDescription.setText(capitalizeFirstLetter(weatherDescription));
//            binding.tvWeatherTemperature.setText(String.format("%.1f°C", temperature));
//            binding.tvWeatherPressure.setText(pressure + " hPa");
//            binding.tvWeatherHumidity.setText(humidity + "%");
//            binding.tvWeatherWindSpeed.setText(windSpeed + " m/s");
//            binding.tvWeatherPrecipitation.setText(precipitation);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    // Helper method to capitalize the first letter of the description
//    private String capitalizeFirstLetter(String input) {
//        if (input == null || input.isEmpty()) return input;
//        return input.substring(0, 1).toUpperCase() + input.substring(1);
//    }
//}
//

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

    private ActivityMainBinding binding;
    private WeatherRepository weatherRepository;
    //private CardView cardDescription, cardTemperature, cardPressure, cardHumidity, cardWindSpeed, cardPrecipitation;
    private final Set<CardView> selectedCards = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        weatherRepository = new WeatherRepository();

//        // Initialize CardViews
//        cardDescription = findViewById(R.id.card_description);
//        cardTemperature = findViewById(R.id.card_temperature);
//        cardPressure = findViewById(R.id.card_pressure);
//        cardHumidity = findViewById(R.id.card_humidity);
//        cardWindSpeed = findViewById(R.id.card_wind_speed);
//        cardPrecipitation = findViewById(R.id.card_precipitation);

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
                        .append("\n$");
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
                // Use runOnUiThread to update the UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Update your UI here with weatherData
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

    private void showError(String errorMessage) {
        // Handle error UI updates (e.g., show a toast or dialog)
        Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
    }
}
