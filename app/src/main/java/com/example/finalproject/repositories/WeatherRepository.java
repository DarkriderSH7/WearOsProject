package com.example.finalproject.repositories;

import com.example.finalproject.models.WeatherData;
import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;

public class WeatherRepository {

    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather?q=";
    private static final String API_KEY = "8048ad7b856423eff0e08e7df8062a9d";
    private final OkHttpClient client;

    public WeatherRepository() {
        client = new OkHttpClient();
    }

    public void fetchWeatherData(String cityName, WeatherCallback callback) {
        String url = BASE_URL + cityName + "&appid=" + API_KEY + "&units=metric";

        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    WeatherData weatherData = new Gson().fromJson(responseData, WeatherData.class);
                    // Notify the activity or UI thread
                    callback.onSuccess(weatherData);
                } else {
                    callback.onError("Failed to fetch data");
                }
            }
        });
    }


    public interface WeatherCallback {
        void onSuccess(WeatherData weatherData);
        void onError(String errorMessage);
    }
}
