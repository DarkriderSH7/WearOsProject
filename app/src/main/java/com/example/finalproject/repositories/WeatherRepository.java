package com.example.finalproject.repositories;

import com.example.finalproject.models.WeatherData;
import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;

public class WeatherRepository {

    // Base URL for OpenWeather API
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather?q=";
    // API key for authentication
    private static final String API_KEY = "8048ad7b856423eff0e08e7df8062a9d";
    private final OkHttpClient client;

    // Constructor initializes the OkHttpClient
    public WeatherRepository() {
        client = new OkHttpClient();
    }

    // Fetch weather data for a specific city and pass the result to a callback
    public void fetchWeatherData(String cityName, WeatherCallback callback) {
        // Build the URL with city name and API key
        String url = BASE_URL + cityName + "&appid=" + API_KEY + "&units=metric";

        // Create a request object
        Request request = new Request.Builder().url(url).build();

        // Make an asynchronous API call
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // If the request fails, pass the error message to the callback
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // If the response is successful, parse the weather data and return it
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    WeatherData weatherData = new Gson().fromJson(responseData, WeatherData.class);
                    callback.onSuccess(weatherData); // Notify success with parsed data
                } else {
                    // If the response is not successful, pass an error message
                    callback.onError("Failed to fetch data");
                }
            }
        });
    }

    // Interface for passing weather data or errors back to the calling activity
    public interface WeatherCallback {
        void onSuccess(WeatherData weatherData); // Success callback with weather data
        void onError(String errorMessage); // Error callback with error message
    }
}
