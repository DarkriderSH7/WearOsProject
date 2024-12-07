package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WeatherFetchActivity extends AppCompatActivity {
    private EditText etCityName;
    private Button btnFetchWeather, btnSendToWatch;
    private TextView tvCityName, tvWeatherDescription, tvWeatherTemperature, tvWeatherPressure;
    private TextView tvWeatherHumidity, tvWeatherWindSpeed, tvWeatherPrecipitation;

    // Set to hold all selected CardViews
    private Set<CardView> selectedCards = new HashSet<>();

    private final String API_KEY = "8048ad7b856423eff0e08e7df8062a9d"; // Replace with your API key
    private final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather?q=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Initialize UI components
        etCityName = findViewById(R.id.et_city_name);
        btnFetchWeather = findViewById(R.id.btn_fetch_weather);
        btnSendToWatch = findViewById(R.id.btn_send_to_watch);
        tvCityName = findViewById(R.id.tv_city_name);
        tvWeatherDescription = findViewById(R.id.tv_weather_description);
        tvWeatherTemperature = findViewById(R.id.tv_weather_temperature);
        tvWeatherPressure = findViewById(R.id.tv_weather_pressure);
        tvWeatherHumidity = findViewById(R.id.tv_weather_humidity);
        tvWeatherWindSpeed = findViewById(R.id.tv_weather_wind_speed);
        tvWeatherPrecipitation = findViewById(R.id.tv_weather_precipitation);

        // Initialize CardViews
        CardView cardDescription = findViewById(R.id.card_description);
        CardView cardTemperature = findViewById(R.id.card_temperature);
        CardView cardPressure = findViewById(R.id.card_pressure);
        CardView cardHumidity = findViewById(R.id.card_humidity);
        CardView cardWindSpeed = findViewById(R.id.card_wind_speed);
        CardView cardPrecipitation = findViewById(R.id.card_precipitation);

        // Set OnClickListener for each CardView
        cardDescription.setOnClickListener(v -> handleCardSelection(cardDescription));
        cardTemperature.setOnClickListener(v -> handleCardSelection(cardTemperature));
        cardPressure.setOnClickListener(v -> handleCardSelection(cardPressure));
        cardHumidity.setOnClickListener(v -> handleCardSelection(cardHumidity));
        cardWindSpeed.setOnClickListener(v -> handleCardSelection(cardWindSpeed));
        cardPrecipitation.setOnClickListener(v -> handleCardSelection(cardPrecipitation));

        // Set OnClickListener for "Send to Watch" Button
        btnSendToWatch.setOnClickListener(v -> {
            if (selectedCards.isEmpty()) {
                Toast.makeText(WeatherFetchActivity.this, "No attributes selected to send.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Build the data to send
            StringBuilder selectedAttributes = new StringBuilder();
            for (CardView card : selectedCards) {
                TextView title = card.findViewById(
                        card.getId() == R.id.card_description ? R.id.tv_weather_description_title :
                                card.getId() == R.id.card_temperature ? R.id.tv_weather_temperature_title :
                                        card.getId() == R.id.card_pressure ? R.id.tv_weather_pressure_title :
                                                card.getId() == R.id.card_humidity ? R.id.tv_weather_humidity_title :
                                                        card.getId() == R.id.card_wind_speed ? R.id.tv_weather_wind_speed_title :
                                                                R.id.tv_weather_precipitation_title
                );
                TextView value = card.findViewById(
                        card.getId() == R.id.card_description ? R.id.tv_weather_description :
                                card.getId() == R.id.card_temperature ? R.id.tv_weather_temperature :
                                        card.getId() == R.id.card_pressure ? R.id.tv_weather_pressure :
                                                card.getId() == R.id.card_humidity ? R.id.tv_weather_humidity :
                                                        card.getId() == R.id.card_wind_speed ? R.id.tv_weather_wind_speed :
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

        btnFetchWeather.setOnClickListener(v -> {
            String cityName = etCityName.getText().toString().trim();
            if (!cityName.isEmpty()) {
                fetchWeatherData(cityName);
            } else {
                Toast.makeText(WeatherFetchActivity.this, "Please enter a city name", Toast.LENGTH_SHORT).show();
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
            selectedCards.remove(card);
        } else {
            // Select the card
            card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.selected_tile));
            selectedCards.add(card);
        }
    }

    private void fetchWeatherData(String cityName) {
        String url = BASE_URL + cityName + "&appid=" + API_KEY + "&units=metric";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Show a loading indicator
        btnFetchWeather.setEnabled(false);
        btnFetchWeather.setText("Fetching...");

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    btnFetchWeather.setEnabled(true);
                    btnFetchWeather.setText("Fetch Weather");
                    Toast.makeText(WeatherFetchActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(() -> {
                    btnFetchWeather.setEnabled(true);
                    btnFetchWeather.setText("Fetch Weather");
                });

                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    runOnUiThread(() -> parseAndDisplayWeather(responseData));
                } else {
                    runOnUiThread(() -> Toast.makeText(WeatherFetchActivity.this, "City not found", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void parseAndDisplayWeather(String responseData) {
        try {
            JsonObject jsonObject = JsonParser.parseString(responseData).getAsJsonObject();
            String cityName = jsonObject.get("name").getAsString();
            String weatherDescription = jsonObject
                    .getAsJsonArray("weather")
                    .get(0)
                    .getAsJsonObject()
                    .get("description")
                    .getAsString();
            double temperature = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
            int pressure = jsonObject.getAsJsonObject("main").get("pressure").getAsInt();
            int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
            double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();

            // Precipitation might be under "rain" or "snow" depending on the weather condition
            String precipitation = "N/A";
            if (jsonObject.has("rain")) {
                JsonObject rain = jsonObject.getAsJsonObject("rain");
                if (rain.has("1h")) {
                    precipitation = rain.get("1h").getAsDouble() + " mm";
                } else if (rain.has("3h")) {
                    precipitation = rain.get("3h").getAsDouble() + " mm";
                }
            } else if (jsonObject.has("snow")) {
                JsonObject snow = jsonObject.getAsJsonObject("snow");
                if (snow.has("1h")) {
                    precipitation = snow.get("1h").getAsDouble() + " mm";
                } else if (snow.has("3h")) {
                    precipitation = snow.get("3h").getAsDouble() + " mm";
                }
            }

            // Update UI with fetched data
            tvCityName.setText("City: " + cityName);
            tvWeatherDescription.setText(capitalizeFirstLetter(weatherDescription));
            tvWeatherTemperature.setText(String.format("%.1f°C", temperature));
            tvWeatherPressure.setText(pressure + " hPa");
            tvWeatherHumidity.setText(humidity + "%");
            tvWeatherWindSpeed.setText(windSpeed + " m/s");
            tvWeatherPrecipitation.setText(precipitation);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method to capitalize the first letter of the description
    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
